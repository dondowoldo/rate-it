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

    async function handleSubmit(event) {
        try {
            event.preventDefault();

            const reviewText = document.querySelector('#review-text-field').value;
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

        } catch (error) {
            console.error(error);
        }
    }

    const form = document.getElementById("review-form");
    form.addEventListener('submit', handleSubmit);
});
