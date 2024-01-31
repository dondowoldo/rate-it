document.addEventListener('DOMContentLoaded', function () {
    updateCharCountAndResize(document.querySelector("#review-text-field").getAttribute("maxlength"));
})

function updateCharCountAndResize(maxLength) {
    const textarea = document.querySelector("#review-text-field");
    const charCount = document.querySelector("#charCount");
    const remainingChars = maxLength - textarea.value.length;
    charCount.textContent = "Characters remaining: " + remainingChars;
    textarea.style.height = "auto";
    textarea.style.height = textarea.scrollHeight + "px";
}

document.addEventListener('DOMContentLoaded', function () {
    const TIMEOUT_DURATION = 2000;

    let previousReviewText = '';

    async function handleSubmit(event) {
        try {
            event.preventDefault();

            const reviewText = document.querySelector('#review-text-field').value;

            if (!reviewText.trim() || reviewText === previousReviewText) {
                displayMessage('Please enter a new review before submitting.', 'info');
                return;  // Stop further execution if the review text is empty or the same
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

            setTimeout(function() {
                $('#review-modal').modal('hide');
            }, TIMEOUT_DURATION);

            previousReviewText = reviewText;

        } catch (error) {
            console.error(error);

            displayMessage('Error sending review. Please try again later.', 'danger');
        }
    }

    function displayMessage(message, type) {
        const charCountElement = document.querySelector('#charCount');

        charCountElement.innerHTML = `<div class="alert alert-${type}" role="alert">${message}</div>`;

        setTimeout(function() {
            charCountElement.innerHTML = '';
        }, TIMEOUT_DURATION);
    }

    const form = document.getElementById("review-form");
    form.addEventListener('submit', handleSubmit);
});
