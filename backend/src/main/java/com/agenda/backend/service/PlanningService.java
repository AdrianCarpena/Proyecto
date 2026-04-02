package com.agenda.backend.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.agenda.backend.algorithm.Planifiable;
import com.agenda.backend.model.BusyHours;
import com.agenda.backend.model.Examen;
import com.agenda.backend.model.StudySession;
import com.agenda.backend.model.Tarea;
import com.agenda.backend.model.User;
import com.agenda.backend.repository.BusyHoursRepository;
import com.agenda.backend.repository.StudySessionRepository;


@Service
public class PlanningService {
	
	@Autowired
	private BusyHoursRepository busyHoursRepository;

	@Autowired
	private StudySessionRepository studySessionRepository;
	
	
	

	public void reubicarSesionesNoHechas(User user) {

	    LocalDate hoy = LocalDate.now();

	    List<StudySession> sesiones = studySessionRepository.findByUser(user);

	    for (StudySession session : sesiones) {

	        // Solo sesiones pasadas y no hechas
	        if (!session.isCheck() && session.getFecha().isBefore(hoy)) {

	            LocalDate dia1 = hoy;
	            LocalDate dia2 = hoy.plusDays(1);

	            int cargaDia1 = calcularCargaDia(user, dia1);
	            int cargaDia2 = calcularCargaDia(user, dia2);

	            LocalDate nuevoDia;

	            if (cargaDia1 <= cargaDia2) {
	                nuevoDia = dia1;
	            } else {
	                nuevoDia = dia2;
	            }

	            session.setFecha(nuevoDia);
	            studySessionRepository.save(session);
	        }
	    }
	}

	//Metodo para calcular la carga de trabajo de un día concreto.
	private int calcularCargaDia(User user, LocalDate fecha) {

	    int carga = 0;

	    // 1. BusyHours
	    List<BusyHours> busyList = busyHoursRepository.findByUserAndFecha(user, fecha);

	    for (BusyHours busy : busyList) {
	        carga += busy.getDuracionHoras();
	    }

	    // 2. StudySessions
	    List<StudySession> sesiones = studySessionRepository.findByUserAndFecha(user, fecha);

	    for (StudySession s : sesiones) {
	        carga += s.getDuracionHoras();
	    }

	    return carga;
	}
	
	
	
	//Metodo donde el algoritmo  genera el plan de trabajo.
	public void generarPlanParaEvento(User user, Planifiable evento) {
		
		//Borramos todas las sesiones asignadas a ese evento por si se crea 2 veces o hay errores que no se duplique.
		if (evento instanceof Examen) {
		    studySessionRepository.deleteByExamen((Examen) evento);
		} else {
		    studySessionRepository.deleteByTarea((Tarea) evento);
		}

	    int horasTotales = calcularHoras(evento);

	    boolean esExamen = evento instanceof Examen;

	    // Reserva de 1h para repaso en examen
	    if (esExamen) {
	        horasTotales -= 1;
	    }

	    List<LocalDate> diasDisponibles = obtenerDiasDisponibles(evento);

	    int horasRestantes = horasTotales;

	    Map<LocalDate, StudySession> sesionesPorDia = new HashMap<>();

	    // VUELTAS:
	    // 6 → sesiones de 2h
	    // 3 → sesiones de 2h
	    // 1 → sesiones de 1h
	    // 0 → fase final iterativa sin restricción
	    int[] distancias = {6, 3, 1, 0};

	    for (int distanciaMinima : distancias) {

	        if (horasRestantes <= 0) break;

	        // Orden global en TODAS las vueltas
	        diasDisponibles.sort(Comparator
	                .comparingInt((LocalDate d) -> calcularCargaDia(user, d))
	                .thenComparing(Comparator.reverseOrder()));

	        // Si estamos en fase final → iterar hasta completar horas
	        if (distanciaMinima == 0) {

	            while (horasRestantes > 0) {

	                boolean progreso = false;

	                for (LocalDate dia : diasDisponibles) {

	                    if (horasRestantes <= 0) break;

	                    StudySession session = sesionesPorDia.get(dia);

	                    if (session == null) {
	                        session = crearSesion(user, evento, dia, 1);
	                        sesionesPorDia.put(dia, session);
	                    } else {
	                        session.setDuracionHoras(session.getDuracionHoras() + 1);
	                    }

	                    horasRestantes--;
	                    progreso = true;
	                }

	                // Si en una iteración no se pudo avanzar → salir (evita bucles infinitos)
	                if (!progreso) break;
	            }

	        } else {

	            // Vueltas restrictivas (6, 3, 1)
	            for (LocalDate dia : diasDisponibles) {

	                if (horasRestantes <= 0) break;

	                // Comprobar distancia mínima
	                if (!cumpleDistancia(dia, sesionesPorDia.keySet(), distanciaMinima)) {
	                    continue;
	                }

	                // Si ya existe sesión en ese día → no tocar en estas vueltas
	                if (sesionesPorDia.containsKey(dia)) {
	                    continue;
	                }

	                int duracionSesion;

	                if (distanciaMinima >= 3) {
	                    if (horasRestantes >= 2) {
	                        duracionSesion = 2;
	                    } else {
	                        duracionSesion=1; // deja esa 1h para la vuelta de 1h o la final
	                    }
	                } else {
	                    duracionSesion = 1;
	                }

	                StudySession session = crearSesion(user, evento, dia, duracionSesion);

	                sesionesPorDia.put(dia, session);

	                horasRestantes -= duracionSesion;
	            }
	        }
	    }

	    // Guardar sesiones
	    for (StudySession session : sesionesPorDia.values()) {
	        studySessionRepository.save(session);
	    }

	    // 📌 Repaso examen (1h día anterior)
	    if (esExamen) {

	        LocalDate diaRepaso = evento.getFecha().minusDays(1);

	        StudySession existente = sesionesPorDia.get(diaRepaso);

	        if (existente != null) {
	            existente.setDuracionHoras(existente.getDuracionHoras() + 1);
	        } else {
	            StudySession repaso = crearSesion(user, evento, diaRepaso, 1);
	            studySessionRepository.save(repaso);
	        }
	    }
	}
	
	
	private boolean cumpleDistancia(LocalDate dia, Set<LocalDate> diasUsados, int distanciaMinima) {

	    for (LocalDate usado : diasUsados) {

	        long diferencia = Math.abs(usado.toEpochDay() - dia.toEpochDay());

	        if (diferencia < distanciaMinima) {
	            return false;
	        }
	    }

	    return true;
	}
	
	
	
	
	private StudySession crearSesion(User user, Planifiable evento, LocalDate dia, int horas) {

	    StudySession session = new StudySession();

	    session.setFecha(dia);
	    session.setDuracionHoras(horas);
	    session.setUser(user);
	    session.setCheck(false);

	    if (evento instanceof Examen) {
	        session.setExamen((Examen) evento);
	    } else {
	        session.setTarea((Tarea) evento);
	    }

	    return session;
	}
	
	//Metodo para saber los dias maximos de antelacion para empezar sesiones
	private int calcularDiasMaximosAntelacion(Planifiable evento) {

	    int horas = calcularHoras(evento);

	    if (horas < 4) {
	        return 7;
	    } else if (horas < 8) {
	        return 18;
	    } else if (horas < 13) {
	        return 25;
	    } else {
	        return horas * 3;
	    }
	}
	
	//Metodo con el que obtenemos los días disponibles para hacer el plan.
	private List<LocalDate> obtenerDiasDisponibles(Planifiable evento) {

	    LocalDate hoy = LocalDate.now();
	    LocalDate fechaEvento = evento.getFecha();

	    // Límite de dias de antelacion
	    int diasMaximosAntelacion = calcularDiasMaximosAntelacion(evento);
	    LocalDate fechaInicioLimite = fechaEvento.minusDays(diasMaximosAntelacion);

	    LocalDate fechaInicio = hoy.isAfter(fechaInicioLimite) ? hoy : fechaInicioLimite;

	    LocalDate fechaFin;

	    if (evento instanceof Examen) {
	        // Excluimos día antes (repaso)
	        fechaFin = fechaEvento.minusDays(1);
	    } else {
	        // Tarea
	        fechaFin = fechaEvento.minusDays(1);
	    }

	    List<LocalDate> dias = new ArrayList<>();

	    LocalDate actual = fechaInicio;

	    while (!actual.isAfter(fechaFin)) {
	        dias.add(actual);
	        actual = actual.plusDays(1);
	    }

	    return dias;
	}	
	
	
	
	
	
	//Metodo para calcular las horas que se necesitan aproximadamente para preparar un examen o una tarea.
	private int calcularHoras(Planifiable evento) {
		
		if (evento.getHorasEstimadas() != null && evento.getHorasEstimadas() > 0) {
		    return evento.getHorasEstimadas();
		}
		
	    int horasBase = 0;

	    // Diferenciamos si es examen o tarea
	    if (evento instanceof Examen) {
	        switch (evento.getDificultad()) {
	            case FACIL:
	                horasBase = 3;
	                break;
	            case MEDIA:
	                horasBase = 5;
	                break;
	            case DIFICIL:
	                horasBase = 8;
	                break;
	        }
	    } else if (evento instanceof Tarea) {
	        switch (evento.getDificultad()) {
	            case FACIL:
	                horasBase = 2;
	                break;
	            case MEDIA:
	                horasBase = 4;
	                break;
	            case DIFICIL:
	                horasBase = 5;
	                break;
	        }
	    }

	    int extra = 0;

	    switch (evento.getPrioridad()) {
	        case MEDIA:
	            extra = 1;
	            break;
	        case ALTA:
	            extra = 2;
	            break;
	        default:
	            extra = 0;
	    }

	    return horasBase + extra;
	}
	
	

}
