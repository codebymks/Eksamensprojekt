//Which statuses an alert is allowed to move to next, by its current status.
const ALLOWED_NEXT_STATUSES = {
    UNDER_REVIEW: ["ACTIVE", "FALSE_ALARM"],
    ACTIVE: ["NOT_ACTIVE"],
    FALSE_ALARM: [],
    NOT_ACTIVE: []
};

//Holds the alerts from the last load, so clicking "Details" doesn't need a second fetch.
let currentAlerts = [];

//Loads every alert from the backend and lists them with a button to view full details.
function loadAlerts() {
    return fetch("/api/alerts")
        .then(response => response.json())
        .then(alerts => {
            currentAlerts = alerts;
            const tableBody = document.getElementById("alerts-body");
            tableBody.innerHTML = "";

            alerts.forEach(alert => {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${alert.id}</td>
                    <td>${alert.epicenterLatitude}</td>
                    <td>${alert.epicenterLongitude}</td>
                    <td>${alert.estimatedMagnitude}</td>
                    <td>${alert.alertStatus}</td>
                    <td><button class="show-details" data-id="${alert.id}">Details</button></td>
                `;
                tableBody.appendChild(row);
            });
        });
}

//Shows one alert's epicenter/magnitude/status, the sensor readings behind it, its actions and its report count.
function showAlertDetails(alertId) {
    const alert = currentAlerts.find(a => a.id === Number(alertId));
    if (!alert) return;

    document.getElementById("no-alert-selected").style.display = "none";
    document.getElementById("alert-details-content").style.display = "block";

    document.getElementById("detail-epicenter").textContent = `${alert.epicenterLatitude}, ${alert.epicenterLongitude}`;
    document.getElementById("detail-magnitude").textContent = alert.estimatedMagnitude;
    document.getElementById("detail-status").textContent = alert.alertStatus;

    const buttons = ALLOWED_NEXT_STATUSES[alert.alertStatus]
        .map(status => `<button data-id="${alert.id}" data-status="${status}">${status}</button>`)
        .join(" ");
    document.getElementById("detail-actions").innerHTML = buttons || "No actions available.";

    document.getElementById("detail-reports").innerHTML =
        `${alert.reportCount} <button class="view-reports" data-id="${alert.id}">View</button>`;

    fetch(`/api/alerts/${alertId}/readings`)
        .then(response => response.json())
        .then(readings => {
            document.getElementById("readings-body").innerHTML = readings.map(reading => `
                <tr>
                    <td>${reading.readingId}</td>
                    <td>${reading.sensor.sensorID}</td>
                    <td>${reading.sensor.latitude}</td>
                    <td>${reading.sensor.longitude}</td>
                    <td>${reading.estimatedDistanceToEpicenterKm}</td>
                    <td>${reading.estimatedMagnitude}</td>
                    <td>${reading.recordedAt}</td>
                </tr>
            `).join("");
        });
}

//Fetches the citizen reports for one alert and shows each one's intensity in a dialog popup.
function showReports(alertId) {
    fetch(`/api/alerts/${alertId}/reports`)
        .then(response => response.json())
        .then(reports => {
            const content = document.getElementById("reports-dialog-content");
            content.innerHTML = reports.length === 0
                ? "No reports yet."
                : reports.map(report => `<div>Report #${report.id}: intensity ${report.intensity}</div>`).join("");
            document.getElementById("reports-dialog").showModal();
        });
}

//Handles clicks anywhere on the page: viewing an alert's details, viewing its reports, or changing its status.
document.addEventListener("click", event => {
    if (event.target.classList.contains("show-details")) {
        showAlertDetails(event.target.dataset.id);
        return;
    }

    if (event.target.classList.contains("view-reports")) {
        showReports(event.target.dataset.id);
        return;
    }

    const status = event.target.dataset.status;
    if (!status) return;

    const id = event.target.dataset.id;
    fetch(`/api/alerts/${id}/status`, {
        method: "PATCH",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ status })
    })
        .then(loadAlerts)
        .then(() => showAlertDetails(id));
});

loadAlerts();
