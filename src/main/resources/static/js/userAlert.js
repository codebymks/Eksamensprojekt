 //Loads active earthquake alerts from the backend and lists them in the table.
fetch("/api/alerts/active", {
    headers: {"Authorization": sessionStorage.getItem("authHeader")}
})
    .then(response => response.json())
    .then(alerts => {
        const tableBody = document.getElementById("alerts-body");
        const noAlertsMessage = document.getElementById("no-alerts-message");

        if (alerts.length === 0) {
            noAlertsMessage.textContent = "There are no active alerts right now.";
            return;
        }

        alerts.forEach(alert => {
            const row = document.createElement("tr");
            row.innerHTML = `
                <td>${alert.id}</td>
                <td>${alert.epicenterLatitude}</td>
                <td>${alert.epicenterLongitude}</td>
                <td>${alert.estimatedMagnitude}</td>
                <td>${alert.area ?? "-"}</td>
                <td>
                    <form class="report-form">
                        <input type="number" class="intensity-input" placeholder="Intensity" required>
                        <button type="submit">Submit report</button>
                    </form>
                    <span class="report-status"></span>
                </td>
            `;
            tableBody.appendChild(row);

            setupReportForm(row, alert.id);
        });
    });