const API = "/api/masini/";

const el = (id) => document.getElementById(id);
const brut = (x) => { el("brut").textContent = typeof x === "string" ? x : JSON.stringify(x, null, 2); };

function stare(text, bun) {
  const s = el("stare");
  s.textContent = text;
  s.className = "stare " + (bun ? "ok" : "rau");
}

async function cerere(cale, optiuni) {
  const r = await fetch(API + cale, optiuni || {});
  const text = await r.text();
  let corp;
  try { corp = JSON.parse(text); } catch (e) { corp = text; }
  return { cod: r.status, corp: corp };
}

function desenizeaza(lista) {
  if (!lista.length) { el("lista").className = "gol"; el("lista").textContent = "nicio masina"; return; }
  const capete = ["externalId", "make", "model", "year", "price"].filter((c) => c in lista[0]);
  const coloane = capete.length ? capete : Object.keys(lista[0]).slice(0, 5);
  el("lista").className = "";
  el("lista").innerHTML =
    "<table><thead><tr>" + coloane.map((c) => "<th>" + c + "</th>").join("") + "</tr></thead><tbody>" +
    lista.slice(0, 15).map((m) => "<tr>" + coloane.map((c) => "<td>" + (m[c] === null || m[c] === undefined ? "-" : m[c]) + "</td>").join("") + "</tr>").join("") +
    "</tbody></table>";
}

el("btn-lista").addEventListener("click", async () => {
  const r = await cerere("cars");
  brut(r.corp);
  const lista = Array.isArray(r.corp) ? r.corp : (r.corp && (r.corp.items || r.corp.cars || r.corp.data)) || [];
  stare(r.cod === 200 ? "am primit " + lista.length + " masini" : "cod " + r.cod, r.cod === 200);
  desenizeaza(lista);
});

el("btn-regen").addEventListener("click", async () => {
  const r = await cerere("cars/regenerate", { method: "POST" });
  brut(r.corp);
  stare(r.cod < 300 ? "sursa regenerata, incarca din nou" : "cod " + r.cod, r.cod < 300);
});
