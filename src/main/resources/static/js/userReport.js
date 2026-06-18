// Wires up an alert row's report form so submitting it sends a citizen report for that alert.
function setupReportForm(row, alertId) {
    const form = row.querySelector(".report-form");
    form.addEventListener("submit", event => submitReport(event, alertId, row));
}

// Sends the intensity typed into a row's form as a citizen report for that alert.
function submitReport(event, alertId, row) {
    event.preventDefault();

    const input = row.querySelector(".intensity-input");
    const status = row.querySelector(".report-status");

    fetch(`/api/alerts/${alertId}/reports`, {
        method: "POST",
        headers: {"Content-Type": "application/json"},
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
