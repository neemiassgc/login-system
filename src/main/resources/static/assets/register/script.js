( () => {
	"user strict";

	const form = document.forms[0],
		emailInput = form[5],
		emailHelper = document.querySelector("#email-helper"),
		submitButton = form[7],
		birthInput = form[2];

	const NOT_ACCEPTABLE = 406, OK = 200;

	window.onkeyup = () => {
		submitButton.disabled = !form.checkValidity();
	};

	emailInput.onfocus = () => {
		emailHelper.classList.add("d-none");
	};

	birthInput.onblur = () => {
		if(birthInput.checkValidity()) {
			let age = 0,
				date = new Date(),
				birth = birthInput.value.split("-").map( (value) => { return parseInt(value) });

			age = date.getFullYear() - birth[0] -
				((birth[1] >= date.getMonth() + 1 && birth[2] >= date.getDate()) ? 0 : 1);

			form[3].value = age;
		}
	};

	submitButton.onclick = () => {
		let request = new XMLHttpRequest();

		request.onreadystatechange = () => {
			if(request.readyState === 4) {
				if(request.status === NOT_ACCEPTABLE) {
					emailHelper.classList.remove("d-none");
				}
				else if(request.status === OK && form.checkValidity()) {
					form.submit();
				}
			}
		};

		request.open("GET", `${window.location.origin}/validate/check-email?email=${emailInput.value}`, true);
		request.send();
	};
})();