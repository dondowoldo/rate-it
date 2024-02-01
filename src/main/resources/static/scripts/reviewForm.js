const TIMEOUT_DURATION = 2000;
document.addEventListener('DOMContentLoaded', async function () {
    updateCharCountAndResize(document.querySelector("#review-text-field").getAttribute("maxlength"));

    const rateButton = document.querySelector("#rate");
    if (!review) {
        rateButton.setAttribute('data-bs-toggle', 'modal');
        rateButton.setAttribute('data-bs-target', '#review-modal');
    }
    const reviewTextField = document.querySelector("#review-text-field");
    reviewTextField.addEventListener('input', function () {
        updateCharCountAndResize(reviewTextField.getAttribute("maxlength"));
    });

    if (review) {
        displayReview(review);
    } else {
        displayReview();
    }

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

            const newReview = await response.json(); // Assuming the server returns the new review object

            // Display the new review instantly
            displayReview(newReview);

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

function displayReview(newReview) {
    const reviewContainer = document.querySelector('.place-review');
    const reviewElement = reviewContainer.querySelector('.review-container');

    updateReviewText(reviewElement, newReview);
    updateReviewTimestamp(reviewElement, newReview);
    updateReviewButtons(reviewElement, newReview);
}

function updateReviewText(reviewElement, newReview) {
    const reviewTextElement = reviewElement.querySelector('h2');
    const reviewContentElement = reviewElement.querySelector('p');
    const textarea = document.querySelector("#review-text-field");

    reviewTextElement.textContent = newReview ? 'Your Review' : 'Review';
    reviewContentElement.textContent = newReview ? newReview.text : 'Rate to add your review!';
    textarea.value = newReview ? newReview.text : '';
}

function updateReviewTimestamp(reviewElement, newReview) {
    let timestampElement = reviewElement.querySelector('.timestamp');

    if (newReview) {
        if (!timestampElement) {
            timestampElement = document.createElement('p');
            timestampElement.classList.add('timestamp');
            reviewElement.appendChild(timestampElement);
        }

        timestampElement.textContent = new Date(newReview.timestamp).toLocaleString();
    } else {
        if (timestampElement) {
            reviewElement.removeChild(timestampElement);
        }
    }
}

function updateReviewButtons(reviewElement, newReview) {
    let editButton = reviewElement.querySelector('.btn-primary');
    let deleteButton = reviewElement.querySelector('.btn-danger');

    if (newReview) {
        updateButton(reviewElement, editButton, 'Edit Review', function () {
            $('#review-modal').modal('show');
        }, ['btn-primary', 'mr-2']); // Additional classes as needed

        updateButton(reviewElement, deleteButton, 'Delete Review', function () {
            deleteReview(); // You can define the deleteReview function as needed
        }, ['btn-danger']);
    } else {
        removeElementIfExist(editButton);
        removeElementIfExist(deleteButton);
    }
}

function updateButton(reviewElement, button, text, clickHandler, classes = []) {
    if (!button) {
        button = document.createElement('button');
        button.classList.add('btn', ...classes); // Adding the specified classes
        button.textContent = text;
        button.addEventListener('click', clickHandler);
        reviewElement.appendChild(button);
    }
}

function removeElementIfExist(element) {
    if (element) {
        element.parentNode.removeChild(element);
    }
}

// Function to handle deleting the review (replace with your actual endpoint)
// Function to handle deleting the review
async function deleteReview() {
    try {
        const response = await fetch(`/api/v1/places/${placeId}/delete-review`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        displayReview();
        const rateButton = document.querySelector("#rate");
        rateButton.setAttribute('data-bs-toggle', 'modal');
        rateButton.setAttribute('data-bs-target', '#review-modal');
        const textarea = document.querySelector("#review-text-field");
        textarea.value = '';
    } catch (error) {
        console.error(error);
    }
}

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