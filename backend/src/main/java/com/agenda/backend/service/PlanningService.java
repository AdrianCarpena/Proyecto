package com.agenda.backend.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.agenda.backend.model.User;

public class PlanningService {
	
	@Autowired
	private BusySlotRepository busySlotRepository;
	
	
	
	
	

	public void checkMissedSessions(User user) {

		LocalDate today = LocalDate.now();

		// Buscar sesiones pasadas no completadas
		List<StudySession> missedSessions = studySessionRepository.findByUserAndDateBeforeAndCompletedFalse(user,
				today);

		for (StudySession session : missedSessions) {

			LocalDate newDate = today;

			// calcular fecha límite (día antes del examen o tarea)
			LocalDate limitDate = null;

			if (session.getExam() != null) {
				limitDate = session.getExam().getExamDate().minusDays(1);
			}

			if (session.getTask() != null) {
				limitDate = session.getTask().getDeadline().minusDays(1);
			}

			// buscar día disponible sin pasar el límite
			while (limitDate != null && !newDate.isAfter(limitDate)) {

				int hoursPlanned = getDailyWorkload(user, newDate);

				if (hoursPlanned < 4) {
					break;
				}

				newDate = newDate.plusDays(1);
			}

			// si se pasó del límite, forzar último día posible
			if (limitDate != null && newDate.isAfter(limitDate)) {
				newDate = limitDate;
			}

			session.setDate(newDate);
			studySessionRepository.save(session);
		}
	}

	private int getDailyWorkload(User user, LocalDate date) {

	    int studyHours = studySessionRepository
	            .findByUserAndDate(user, date)
	            .stream()
	            .mapToInt(StudySession::getHours)
	            .sum();

	    int busyHours = busySlotRepository
	            .findByUserAndDate(user, date)
	            .stream()
	            .mapToInt(BusySlot::getHours)
	            .sum();

	    return studyHours + busyHours;
	}

	public void calculatePlan(Planifiable evento) {
	    User user = evento.getUser();
	    LocalDate hoy = LocalDate.now();
	    LocalDate fechaLimite = evento.getFecha().minusDays(1); // día anterior
	    int horasTotales = evento.getHorasEstimadas();

	    if (horasTotales <= 0 || !hoy.isBefore(fechaLimite.plusDays(1))) return;

	    // 1️⃣ Reservamos 1h para repaso el día anterior
	    int horasParaRepartir = horasTotales - 1;

	    // 2️⃣ Lista de días disponibles
	    List<LocalDate> diasDisponibles = new ArrayList<>();
	    for (LocalDate d = hoy; !d.isAfter(fechaLimite); d = d.plusDays(1)) {
	        diasDisponibles.add(d);
	    }

	    if (diasDisponibles.isEmpty()) return;

	    // 3️⃣ Calculamos carga actual de cada día
	    Map<LocalDate, Integer> cargaDias = new HashMap<>();
	    for (LocalDate dia : diasDisponibles) {
	        int busy = getBusyHours(user, dia); // horas ocupadas ya existentes
	        cargaDias.put(dia, busy);
	    }

	    int horasRestantes = horasParaRepartir;

	    // 4️⃣ Distribuimos horas de forma desigual
	    while (horasRestantes > 0 && !diasDisponibles.isEmpty()) {
	        LocalDate mejorDia = diasDisponibles.stream()
	                .min(Comparator.comparingInt(cargaDias::get))
	                .get();

	        int cargaActual = cargaDias.get(mejorDia);
	        int maxDia = 4; // máximo de estudio recomendado
	        int asignar = Math.min(2, horasRestantes); // 1-2h por iteración

	        if (cargaActual + asignar > maxDia) {
	            diasDisponibles.remove(mejorDia);
	            continue;
	        }

	        // Creamos sesión vinculada al evento
	        StudySession session = StudySession.builder()
	                .user(user)
	                .planifiable(evento)  // el objeto Tarea o Examen
	                .date(mejorDia)
	                .hours(asignar)
	                .description("Estudio: " + evento.getTitulo())
	                .build();
	        studySessionRepository.save(session);

	        cargaDias.put(mejorDia, cargaActual + asignar);
	        horasRestantes -= asignar;
	    }

	    // 5️⃣ 1h de repaso el día anterior al evento
	    StudySession repaso = StudySession.builder()
	            .user(user)
	            .planifiable(evento)
	            .date(evento.getFecha().minusDays(1))
	            .hours(1)
	            .description("Repaso final: " + evento.getTitulo())
	            .build();
	    studySessionRepository.save(repaso);
	}
	

	private void distributeStudyHours(User user, int hoursNeeded, LocalDate start, LocalDate limit, Exam exam,
			Task task) {

		LocalDate current = start;

		while (hoursNeeded > 0 && (current.isBefore(limit) || current.equals(limit))) {

			int workload = getDailyWorkload(user, current);

			if (workload < 4) {

				StudySession session = new StudySession();

				session.setUser(user);
				session.setDate(current);
				session.setHours(1);
				session.setCompleted(false);

				session.setExam(exam);
				session.setTask(task);

				studySessionRepository.save(session);

				hoursNeeded--;
			}

			current = current.plusDays(1);
		}
	}
	
	private int calculateRequiredHours(String difficulty, String priority) {

	    int baseHours;

	    switch (difficulty.toLowerCase()) {

	        case "facil":
	            baseHours = 2;
	            break;

	        case "medio":
	            baseHours = 5;
	            break;

	        case "dificil":
	            baseHours = 8;
	            break;

	        default:
	            baseHours = 4;
	    }

	    double multiplier;

	    switch (priority.toLowerCase()) {

	        case "alta":
	            multiplier = 1.5;
	            break;

	        case "media":
	            multiplier = 1.25;
	            break;

	        default:
	            multiplier = 1.0;
	    }

	    return (int) Math.round(baseHours * multiplier);
	}

}
