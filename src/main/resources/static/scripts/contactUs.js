function submitForm() {
    const form = document.getElementById('contactForm');
    const formData = new FormData(form);

    const jsonData = {};
    formData.forEach((value, inputName) => {
        jsonData[inputName] = value;
    });

    fetch('/contact-us', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(jsonData)
    })
        .then(response => {
            if (response.ok) {
                form.reset();
                const successful = document.querySelector('.successful');
                successful.style.marginTop = '1rem';
                successful.innerHTML = 'Email was sent successfully';
            } else {
                alert('Failed to send email. Please try again.\nError: ' + response.statusText);
            }
        })
        .catch(error => {
            console.error('Fetch error:', error);
        });
}