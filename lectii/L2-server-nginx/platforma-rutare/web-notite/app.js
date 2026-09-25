const API = "/api/notite/";
let token = "";

const el = (id) => document.getElementById(id);
const brut = (x) => { el("brut").textContent = typeof x === "string" ? x : JSON.stringify(x, null, 2); };

function stare(text, bun) {
  const s = el("stare");
  s.textContent = text;
  s.className = "stare " + (bun ? "ok" : "rau");
  el("btn-lista").disabled = !bun;
  el("btn-adauga").disabled = !bun;
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
      email: el("email").value, password: el("parola").value,
      firstName: "Elev", lastName: "Nou"
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

function extrage(corp) {
  if (Array.isArray(corp)) return corp;
  if (corp && typeof corp === "object") {
    for (const cheie of Object.keys(corp)) {
      if (Array.isArray(corp[cheie])) return corp[cheie];
    }
  }
  return [];
}

async function categorie() {
  const r = await cerere("categories");
  const lista = extrage(r.corp);
  if (lista.length) return lista[0].id;
  const nou = await cerere("categories", {
    method: "POST",
    body: JSON.stringify({ name: "General" })
  });
  return nou.corp && nou.corp.id;
}

el("btn-adauga").addEventListener("click", async () => {
  const titlu = el("titlu").value.trim() || "Notita fara titlu";
  const categoryId = await categorie();
  const r = await cerere("notes", {
    method: "POST",
    body: JSON.stringify({
      title: titlu,
      content: "scrisa din frontend, prin nginx",
      categoryId: categoryId,
      isFavorite: false,
      date: new Date().toISOString()
    })
  });
  brut(r.corp);
  if (r.cod < 300) { el("titlu").value = ""; el("btn-lista").click(); }
});

el("btn-lista").addEventListener("click", async () => {
  const r = await cerere("notes");
  brut(r.corp);
  const lista = extrage(r.corp);
  if (!lista.length) { el("lista").className = "gol"; el("lista").textContent = "nicio notita (cod " + r.cod + ")"; return; }
  el("lista").className = "";
  el("lista").innerHTML =
    "<table><thead><tr><th>titlu</th><th>continut</th></tr></thead><tbody>" +
    lista.map((n) => "<tr><td>" + (n.title || "-") + "</td><td>" + (n.content || "-") + "</td></tr>").join("") +
    "</tbody></table>";
});
