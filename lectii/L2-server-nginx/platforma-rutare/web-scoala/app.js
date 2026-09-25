const API = "/api/scoala/";
let token = "";

const el = (id) => document.getElementById(id);
const brut = (x) => { el("brut").textContent = typeof x === "string" ? x : JSON.stringify(x, null, 2); };

function stare(text, bun) {
  const s = el("stare");
  s.textContent = text;
  s.className = "stare " + (bun ? "ok" : "rau");
  el("btn-cursuri").disabled = !bun;
}

async function cerere(cale, optiuni) {
  const o = optiuni || {};
  o.headers = Object.assign({ "Content-Type": "application/json" }, o.headers || {});
  if (token) o.headers["Authorization"] = "Bearer " + token;
  const r = await fetch(API + cale, o);
  const text = await r.text();
  let corp;
  try { corp = JSON.parse(text); } catch (e) { corp = text; }
  return { cod: r.status, corp: corp };
}

el("btn-cont").addEventListener("click", async () => {
  const r = await cerere("auth/register", {
    method: "POST",
    body: JSON.stringify({
      firstName: "Elev", lastName: "Nou",
      email: el("email").value, password: el("parola").value,
      phoneNumber: "0712345678"
    })
  });
  brut(r.corp);
  stare(r.cod === 201 ? "cont creat, acum autentifica-te" : "cont neCreat (cod " + r.cod + ")", false);
});

el("btn-login").addEventListener("click", async () => {
  const r = await cerere("auth/login", {
    method: "POST",
    body: JSON.stringify({ email: el("email").value, password: el("parola").value })
  });
  brut(r.corp);
  token = (r.corp && (r.corp.token || r.corp.accessToken)) || "";
  stare(token ? "autentificat" : "autentificare esuata (cod " + r.cod + ")", !!token);
});

el("btn-cursuri").addEventListener("click", async () => {
  const r = await cerere("courses");
  brut(r.corp);
  const lista = Array.isArray(r.corp) ? r.corp : (r.corp && r.corp.content) || [];
  if (!lista.length) { el("cursuri").className = "gol"; el("cursuri").textContent = "niciun curs (cod " + r.cod + ")"; return; }
  const capete = Object.keys(lista[0]).slice(0, 4);
  el("cursuri").className = "";
  el("cursuri").innerHTML =
    "<table><thead><tr>" + capete.map((c) => "<th>" + c + "</th>").join("") + "</tr></thead><tbody>" +
    lista.map((x) => "<tr>" + capete.map((c) => "<td>" + (x[c] === null ? "-" : x[c]) + "</td>").join("") + "</tr>").join("") +
    "</tbody></table>";
});
