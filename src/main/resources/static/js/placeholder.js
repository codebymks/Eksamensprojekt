fetch("/api")
    .then(res => res.json())
    .then(data => {
        const list = document.getElementById("list");
        data.forEach(item => {
            const li = document.createElement("li");
            li.textContent = `${item.test}`;
            list.appendChild(li);
        });
    });