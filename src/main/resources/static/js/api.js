async function api(url, method = 'GET', body) {
  try {
    const options = { method, headers: { 'Content-Type': 'application/json' } };
    if (body !== undefined) options.body = JSON.stringify(body);
    const response = await fetch(url, options);
    const text = await response.text();
    let data = {};
    try { data = text ? JSON.parse(text) : {}; } catch (_) { data = { message: text || 'Unexpected server response' }; }
    if (!response.ok) {
      return { message: data.message || `Request failed (${response.status})`, status: response.status, ...data };
    }
    return data;
  } catch (error) {
    return { message: `Unable to connect to KARPAVAI.AI server: ${error.message}` };
  }
}

async function loginForm(admin) {
  const form = document.getElementById('login');
  if (!form) return;
  form.onsubmit = async e => {
    e.preventDefault();
    const emailEl = document.getElementById('email');
    const passwordEl = document.getElementById('password');
    const msgEl = document.getElementById('msg');
    const r = await api('/api/auth/login', 'POST', {
      email: emailEl.value.trim().toLowerCase(),
      password: passwordEl.value
    });
    if (r.redirect) location.href = r.redirect;
    else if (msgEl) msgEl.textContent = r.message || 'Login failed';
  };
}

async function loadStats() {
  const s = await api('/api/public/stats');
  const stats = document.querySelector('#stats');
  if (stats) stats.innerHTML = `<div><b>${s.companies ?? 0}+</b><span>Companies</span></div><div><b>${s.questions ?? 0}+</b><span>Questions</span></div><div><b>${s.experiences ?? 0}+</b><span>Interview Experiences</span></div><div><b>10</b><span>Years of Data Architecture</span></div>`;
}
