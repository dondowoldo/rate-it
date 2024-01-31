const TIMEOUT_DURATION = 2000;
document.addEventListener('DOMContentLoaded', function () {
    updateCharCountAndResize(document.querySelector("#review-text-field").getAttribute("maxlength"));

    const rateButton = document.querySelector("#rate");
    rateButton.setAttribute('data-bs-toggle', 'modal');
    rateButton.setAttribute('data-bs-target', '#review-modal');

    const reviewTextField = document.querySelector("#review-text-field");
    reviewTextField.addEventListener('input', function () {
        updateCharCountAndResize(reviewTextField.getAttribute("maxlength"));
    });

    async function handleSubmit(event) {
        try {
            event.preventDefault();

            const reviewText = reviewTextField.value;

            if (!reviewText.trim()) {
                displayMessage('Please enter a new review before submitting.', 'secondary');
                return;
            }

            const response = await fetch(`/api/v1/places/${placeId}/review`, {
                method: 'POST',
                body: reviewText,
                headers: {
                    'Content-Type': 'text/plain'
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const responseData = await response.text();
            console.log(responseData);

            displayMessage('Review sent successfully!', 'success');

            rateButton.removeAttribute('data-bs-toggle');
            rateButton.removeAttribute('data-bs-target');

            setTimeout(function () {
                $('#review-modal').modal('hide');
            }, TIMEOUT_DURATION);

            previousReviewText = reviewText;

        } catch (error) {
            console.error(error);

            displayMessage('Error sending review.', 'danger');
        }
    }

    const form = document.getElementById("review-form");
    form.addEventListener('submit', handleSubmit);
});

function displayMessage(message, type) {
    const charCountElement = document.querySelector('#charCount');

    charCountElement.innerHTML = `<div class="alert alert-${type}" role="alert">${message}</div>`;

    setTimeout(function () {
        charCountElement.innerHTML = '';
    }, TIMEOUT_DURATION);
}

function updateCharCountAndResize(maxLength) {
    const textarea = document.querySelector("#review-text-field");
    const charCount = document.querySelector("#charCount");
    const remainingChars = maxLength - textarea.value.length;
    charCount.textContent = "Characters remaining: " + remainingChars;
    textarea.style.height = textarea.scrollHeight + "px";
}