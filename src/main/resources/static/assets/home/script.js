( () => {
	"user strict";

	const buttons = $("button"),
		editButton = buttons[0],
		logoutButton = buttons[1],
		saveButton = buttons[3],
		birthInput = $("#birthday-input")[0];


	editButton.onclick = () => {
		$("#edit-modal").modal("show").on("shown.bs.modal", () => {
			let cols = $("td"), inputs = $("input");

			for(let i = 0; i < cols.length; i++) {
				inputs[i].value = cols[i].innerHTML;
			}
		});
	};

	birthInput.onblur = () => {
		let ageInput = $("#age-input")[0];

		try {
			if(birthInput.value.match(/\d{2}\/\d{2}\/\d{4}/g)[0] === birthInput.value) {
				let age = 0,
					date = new Date(),
					birth = birthInput.value.split("/").map( (value) => { return parseInt(value) });

				ageInput.value = (birth[0] <= 31 && birth[0] > 0 && birth[1] <= 12 && birth[1] > 0) ?
					date.getFullYear() - birth[2] -
					((birth[1] >= date.getMonth() + 1 && birth[0] >= date.getDate()) ? 0 : 1) : "";
			}
		}
		catch(err) {
			ageInput.value = "";
		}
	};

	logoutButton.onclick = () => {
		let logoutRequest = new XMLHttpRequest();

		logoutRequest.onreadystatechange = () => {
			if(logoutRequest.readyState == 4 && logoutRequest.status == 200) {
				location.assign("/login");
			}
		};

		logoutRequest.open("GET", `${window.location.origin}/session/logout`, true);
		logoutRequest.send();
	};

	function updateRequest() {
		let request = new XMLHttpRequest(),
			inputs = $("input"), cols = $("td");

		let obj = {
			name: inputs[0].value,
			surname: inputs[1].value,
			birthday: inputs[2].value,
			age: inputs[3].value,
			color: inputs[4].value,
			email: inputs[5].value,
			password: inputs[6].value
		};

		request.onreadystatechange = () => {
			if(request.readyState === 4) {
				if(request.status === 200) {
					for(let i = 0; i < cols.length; i++) {
						cols[i].innerHTML = inputs[i].value;
					}

					$("h1")[0].innerHTML = `Hello, <span>${obj.name} ${obj.surname}</span>`;
					return;
				}
				alert(request.responseText);
			}
		};

		request.open("PUT", `${window.location.origin}/session/update`, true);
		request.setRequestHeader("Content-Type", "application/json");
		request.send(JSON.stringify(obj));
	}


	saveButton.onclick = () => {
		let emailRequest = new XMLHttpRequest();

		emailRequest.onreadystatechange = () => {
			if(emailRequest.readyState === 4) {
				if(emailRequest.status === 406 /*not acceptable*/) {
					$("#email-helper")[0].classList.remove("d-none");
				}
				else if(emailRequest.status === 200) {
					$("#edit-modal").modal("hide");
					updateRequest();
				}
			}
		};

		emailRequest.open("GET", `${window.location.origin}/validate/check-email?email=${$("#email-input")[0].value}`, true);
		emailRequest.send();
	};

	$("#email-input")[0].onfocus = () => {
		$("#email-helper")[0].classList.add("d-none");
	};
})()