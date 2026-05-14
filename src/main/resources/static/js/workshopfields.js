document.querySelector("#workshop").addEventListener("change", function () {
        const field = document.querySelector(".workshop-dimensions")
        const show = this.value === "WITH";

            field.style.display = show ? "block" : "none";
    }
)