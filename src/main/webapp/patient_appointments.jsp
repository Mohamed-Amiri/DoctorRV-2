<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Gestion des Rendez-vous</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
<div class="container mt-4">
    <h1>Gestion des Rendez-vous</h1>

    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>

    <!-- Filtres -->
    <div class="card mb-4">
        <div class="card-header">
            <h2>Filtres</h2>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/doctor/appointments" method="get">
                <div class="form-row">
                    <div class="form-group col-md-4">
                        <label for="filterDate">Date</label>
                        <input type="date" class="form-control" id="filterDate" name="filterDate" value="${param.filterDate}">
                    </div>
                    <div class="form-group col-md-4">
                        <label for="filterStatus">Statut</label>
                        <select class="form-control" id="filterStatus" name="filterStatus">
                            <option value="">Tous les statuts</option>
                            <option value="SCHEDULED" ${param.filterStatus == 'SCHEDULED' ? 'selected' : ''}>Programmé</option>
                            <option value="CANCELLED" ${param.filterStatus == 'CANCELLED' ? 'selected' : ''}>Annulé</option>
                            <option value="COMPLETED" ${param.filterStatus == 'COMPLETED' ? 'selected' : ''}>Terminé</option>
                        </select>
                    </div>
                    <div class="form-group col-md-4 d-flex align-items-end">
                        <button type="submit" class="btn btn-primary mr-2">Filtrer</button>
                        <a href="${pageContext.request.contextPath}/doctor/appointments" class="btn btn-secondary">Réinitialiser</a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Liste des rendez-vous -->
    <div class="card">
        <div class="card-header">
            <h2>Mes rendez-vous à venir</h2>
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
                            <th>Patient</th>
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
                                <td>${appointment.patient.firstName} ${appointment.patient.lastName}</td>
                                <td>
                                    <div><strong>Email:</strong> <a href="mailto:${appointment.patient.email}">${appointment.patient.email}</a></div>
                                    <div><strong>Tél:</strong> ${appointment.patient.phone}</div>
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
                                    <div class="btn-group" role="group">
                                        <c:if test="${appointment.status == 'SCHEDULED'}">
                                            <form action="${pageContext.request.contextPath}/doctor/appointments" method="post" class="mr-1" onsubmit="return confirm('Êtes-vous sûr de vouloir annuler ce rendez-vous?');">
                                                <input type="hidden" name="action" value="cancel">
                                                <input type="hidden" name="appointmentId" value="${appointment.id}">
                                                <button type="submit" class="btn btn-sm btn-danger">Annuler</button>
                                            </form>
                                            <form action="${pageContext.request.contextPath}/doctor/appointments" method="post" onsubmit="return confirm('Marquer ce rendez-vous comme terminé?');">
                                                <input type="hidden" name="action" value="complete">
                                                <input type="hidden" name="appointmentId" value="${appointment.id}">
                                                <button type="submit" class="btn btn-sm btn-success">Terminer</button>
                                            </form>
                                        </c:if>
                                    </div>
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
</body>
</html>