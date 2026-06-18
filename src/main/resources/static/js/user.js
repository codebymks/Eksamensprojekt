//Wires up an alert row's report form so submitting it sends a citizen report for that alert.
function setupReportForm(row, alertId) {
    const form = row.querySelector(".report-form");
    form.addEventListener("submit", event => submitReport(event, alertId, row));
}

//Sends the intensity typed into a row's form as a citizen report for that alert.
function submitReport(event, alertId, row) {
    event.preventDefault();

    const input = row.querySelector(".intensity-input");
    const status = row.querySelector(".report-status");

    fetch(`/api/alerts/${alertId}/reports`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": sessionStorage.getItem("authHeader")
        },
        body: JSON.stringify({intensity: Number(input.value)})
    })
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to submit report");
            }
            status.textContent = "Thanks for your report!";
            input.value = "";
        })
        .catch(() => {
            status.textContent = "Could not submit report, please try again.";
        });
}

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
                        <input type="number" min="1" max="10" class="intensity-input" placeholder="Intensity on Richter Scale" required>
                        <button type="submit">Submit report</button>
                    </form>
                    <span class="report-status"></span>
                </td>
            `;
            tableBody.appendChild(row);

            setupReportForm(row, alert.id);
        });
    });