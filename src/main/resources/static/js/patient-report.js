document.addEventListener("DOMContentLoaded", () => {
    // Get the modal, buttons, and close span
    const modal = document.getElementById("alertModal");
    const closeAlertBtn = document.getElementById("closeAlertBtn");
    const closeSpan = document.querySelector(".close-btn");

    // Close the modal when the "Close" button is clicked
    closeAlertBtn.addEventListener("click", () => {
        modal.classList.remove("d-block");
        modal.classList.add("d-none");
    });

    // Close the modal when the "x" button is clicked
    closeSpan.addEventListener("click", () => {
        modal.classList.remove("d-block");
        modal.classList.add("d-none");
    });
});

function addItem(sectionId, encounterId, fields) {
    const fieldsArray = fields.match(/\w+/g);

    const accordionId = `${sectionId}-${encounterId}`;
    const accordion = document.getElementById(accordionId);
    const ul = accordion.querySelector('ul');
    const index = ul.children.length;

    const li = document.createElement('li');
    li.className = "mb-3 p-3 border rounded bg-white shadow-sm";

    const row = document.createElement('div');
    row.className = 'row g-3';

    fieldsArray.forEach(field => {
        // input fields
        const col = document.createElement('div');
        col.className = 'col-md-6';

        const label = document.createElement('label');
        label.className = 'form-label fw-bold';
        label.setAttribute('for', `${field}-${index}`);
        label.textContent = `${field}:`;

        const input = document.createElement('input');
        input.type = 'text';
        input.className = 'form-control';
        input.id = `${field}-${index}`;
        input.name = `${sectionId.toLowerCase()}[${index}].${field}`;

        col.appendChild(label);
        col.appendChild(input);
        row.appendChild(col);
    });
    // remove button
    const buttonCol = document.createElement('div');
    buttonCol.className = 'col-12 text-end';

    const button = document.createElement('button');
    button.type = 'button';
    button.className = 'btn btn-danger btn-sm';
    button.textContent = 'Remove';
    button.onclick = function () {
        remove(button); // Attach the remove function
    };
    
    buttonCol.appendChild(button);
    row.appendChild(buttonCol);

    li.appendChild(row);
    ul.appendChild(li);
}

function toggleAccordion(id, level) {
    const panel = document.getElementById(id);
    const isOpen = panel.style.display === "block";

    // Close all panels of the same level
    const panels = document.querySelectorAll(`.panel[data-level="${level}"]`);
    panels.forEach(p => {
        if (p !== panel) p.style.display = "none";
    });

    // Toggle the clicked panel
    panel.style.display = isOpen ? "none" : "block";

    // Update active state for buttons at the same level
    const buttons = document.querySelectorAll(`.accordion[data-level="${level}"]`);
    buttons.forEach(btn => btn.classList.remove("active"));

    if (!isOpen) {
        document.querySelector(`[onclick*='${id}']`).classList.add("active");
    }
}

function remove(button) {
    const listItem = button.closest('li'); // Find the closest <li> ancestor of the button
    if (listItem) {
        listItem.remove(); // Remove the <li> element
    }
}

function submitForm() {
    console.log("Custom submit logic triggered!");
    const modal = document.getElementById("alertModal");
    modal.classList.remove("d-none");
    modal.classList.add("d-block");

    /*
    const form = document.getElementById('form');
    const formData = new FormData(form);

    fetch('/patient-report', {
        method: 'POST',
        body: formData
    }).then(response => {
        if (response.ok) {
            modal.classList.remove("d-none");
            modal.classList.add("d-block");
        }
    });
    */
}