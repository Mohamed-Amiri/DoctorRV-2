<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Mes Rendez-vous</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<div class="container mt-4">
    <h1>Mes Rendez-vous</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <!-- Formulaire de prise de rendez-vous -->
    <div class="card mb-4">
        <div class="card-header">
            <h2>Prendre un rendez-vous</h2>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/patient/appointments" method="post">
                <input type="hidden" name="action" value="create">

                <div class="form-group">
                    <label for="doctorId">Médecin</label>
                    <select class="form-control" id="doctorId" name="doctorId" required>
                        <option value="">-- Choisir un médecin --</option>
                        <c:forEach var="doctor" items="${doctors}">
                            <option value="${doctor.id}" data-email="${doctor.email}" data-phone="${doctor.phone}">
                                Dr. ${doctor.firstName} ${doctor.lastName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Informations sur le médecin sélectionné -->
                <div id="doctorInfo" class="card mb-3" style="display: none;">
                    <div class="card-body">
                        <h5 class="card-title">Informations du médecin</h5>
                        <p><strong>Email:</strong> <span id="doctorEmail"></span></p>
                        <p><strong>Téléphone:</strong> <span id="doctorPhone"></span></p>
                    </div>
                </div>

                <div class="form-group">
                    <label for="date">Date</label>
                    <input type="date" class="form-control" id="date" name="date" required min="${today}">
                </div>

                <div class="form-group">
                    <label for="time">Heure</label>
                    <input type="time" class="form-control" id="time" name="time" required>
                </div>

                <div class="form-group">
                    <label for="reason">Motif</label>
                    <textarea class="form-control" id="reason" name="reason" rows="3" required maxlength="500"></textarea>
                </div>

                <button type="submit" class="btn btn-primary">Réserver</button>
            </form>
        </div>
    </div>

    <!-- Liste des rendez-vous -->
    <div class="card">
        <div class="card-header">
            <h2>Mes rendez-vous prévus</h2>
        </div>
        <div class="card-body">
            <c:if test="${empty appointments}">
                <p class="text-muted">Vous n'avez pas de rendez-vous programmés.</p>
            </c:if>

            <c:if test="${not empty appointments}">
                <div class="table-responsive">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Heure</th>
                            <th>Médecin</th>
                            <th>Contact</th>
                            <th>Motif</th>
                            <th>Statut</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="appointment" items="${appointments}">
                            <tr>
                                <td>${appointment.formattedDate}</td>
                                <td>${appointment.formattedTime}</td>
                                <td>Dr. ${appointment.doctor.firstName} ${appointment.doctor.lastName}</td>
                                <td>
                                    <div><strong>Email:</strong> <a href="mailto:${appointment.doctor.email}">${appointment.doctor.email}</a></div>
                                    <div><strong>Tél:</strong> ${appointment.doctor.phone}</div>
                                </td>
                                <td>${appointment.reason}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${appointment.status == 'SCHEDULED'}">
                                            <span class="badge badge-primary">Programmé</span>
                                        </c:when>
                                        <c:when test="${appointment.status == 'CANCELLED'}">
                                            <span class="badge badge-danger">Annulé</span>
                                        </c:when>
                                        <c:when test="${appointment.status == 'COMPLETED'}">
                                            <span class="badge badge-success">Terminé</span>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:if test="${appointment.status == 'SCHEDULED'}">
                                        <form action="${pageContext.request.contextPath}/patient/appointments" method="post" onsubmit="return confirm('Êtes-vous sûr de vouloir annuler ce rendez-vous?');">
                                            <input type="hidden" name="action" value="cancel">
                                            <input type="hidden" name="appointmentId" value="${appointment.id}">
                                            <button type="submit" class="btn btn-sm btn-danger">Annuler</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Set min date to today
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('date').setAttribute('min', today);

        // Afficher les informations du médecin sélectionné
        const doctorSelect = document.getElementById('doctorId');
        const doctorInfo = document.getElementById('doctorInfo');
        const doctorEmail = document.getElementById('doctorEmail');
        const doctorPhone = document.getElementById('doctorPhone');

        doctorSelect.addEventListener('change', function() {
            const selectedOption = this.options[this.selectedIndex];

            if (this.value) {
                // Récupérer les données de l'option sélectionnée
                const email = selectedOption.getAttribute('data-email');
                const phone = selectedOption.getAttribute('data-phone');

                // Mettre à jour les informations affichées
                doctorEmail.textContent = email;
                doctorPhone.textContent = phone;

                // Afficher la carte d'information
                doctorInfo.style.display = 'block';
            } else {
                // Masquer la carte si aucun médecin n'est sélectionné
                doctorInfo.style.display = 'none';
            }
        });
    });
</script>
</body>
</html>