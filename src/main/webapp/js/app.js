(function(){
  const form = document.getElementById('searchForm');
  const cityInput = document.getElementById('cityInput');
  const geoBtn = document.getElementById('geoBtn');
  const card = document.getElementById('card');
  const result = document.getElementById('result');
  const errorBox = document.getElementById('error');

  // Animate card in on load
  requestAnimationFrame(() => {
    card.classList.remove('fade');
    card.classList.add('show');
  });

  // ---- utility: infer theme from server data ----
  function inferThemeFromData(d){
    if(!d) return 'theme-sun';
    // prefer explicit flags
    if(d.rain === true) return 'theme-rain';
    if(d.snow === true) return 'theme-snow';

    const desc = (d.description || '').toLowerCase();
    if(/rain|drizzle|shower|thunder/i.test(desc)) return 'theme-rain';
    if(/snow|sleet|blizzard/i.test(desc)) return 'theme-snow';
    if(/cloud/i.test(desc)) return 'theme-clouds';
    if(/clear|sun|sunny/i.test(desc)) return 'theme-sun';
    // fallback
    return 'theme-sun';
  }

  // ---- THEME HANDLING ----
  function setTheme(theme) {
    // normalize: expect values like 'theme-sun', 'theme-rain', etc.
    document.body.classList.remove('theme-sun','theme-clouds','theme-rain','theme-snow');
    document.body.classList.add(theme || 'theme-sun');

    // Toggle precip layers (guard in case elements missing)
    const rainLayerEl = document.querySelector('.rain-layer');
    const snowLayerEl = document.querySelector('.snow-layer');
    if(rainLayerEl) rainLayerEl.style.display = (theme === 'theme-rain') ? 'block' : 'none';
    if(snowLayerEl) snowLayerEl.style.display = (theme === 'theme-snow') ? 'block' : 'none';

    // restart particles according to theme
    launchParticles(theme);
  }

  // Initialize theme from server-side (injected by JSP as window.initialTheme)
  if (window.initialTheme) {
    setTheme(window.initialTheme);
  }

  // ---- ERROR HANDLING ----
  function showError(msg) {
    errorBox.textContent = msg || 'Something went wrong';
    errorBox.style.display = 'block';
    result.style.display = 'none';
  }
  function clearError(){ errorBox.style.display = 'none'; }

  function htmlEscape(s){
    return (s||'').replace(/[&<>"']/g, c=>({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
  }

  // ---- FETCH WEATHER ----
  async function fetchWeather(params) {
    clearError();
    card.classList.remove('show');
    card.classList.add('fade');

    const url = 'weather?ajax=true&' + new URLSearchParams(params).toString();
    try {
      const res = await fetch(url, { headers: { 'Accept':'application/json' }});
      if(!res.ok){
        const errText = await res.text().catch(()=> '');
        throw new Error(errText || ('HTTP ' + res.status));
      }
      const data = await res.json();
      updateUI(data);
    } catch (err) {
      showError(err.message || 'Failed to fetch');
    } finally {
      // transition back in
      requestAnimationFrame(() => {
        card.classList.remove('fade');
        card.classList.add('show');
      });
    }
  }

  // ---- UPDATE UI ----
  function updateUI(d){
    if(!d || d.error){
      showError(d && d.error ? d.error : 'No data');
      return;
    }

    // derive theme: prefer server-sent d.theme, otherwise infer locally
    const theme = d.theme || inferThemeFromData(d);
    setTheme(theme);

    result.style.display = 'block';
    result.innerHTML = `
      <div class="city">${htmlEscape(d.city || '')}, ${htmlEscape(d.country || '')}</div>
      <div class="temp">
        <span class="t">${Math.round(d.tempC)}°C</span>
        <span class="feels">feels ${Math.round(d.feelsLikeC)}°C</span>
      </div>
      <div class="desc">${htmlEscape(d.description || '')}</div>
      <div class="meta">
        <span>Humidity ${d.humidity}%</span>
        <span>Wind ${Math.round(d.windKph)} km/h</span>
      </div>
    `;
    // (particles already launched inside setTheme)
  }

  // ---- FORM EVENTS ----
  form.addEventListener('submit', (e)=>{
    e.preventDefault();
    const city = (cityInput.value || '').trim();
    if(!city) { showError('Please type a city'); return; }
    fetchWeather({ city });
  });

  geoBtn.addEventListener('click', ()=>{
    if(!navigator.geolocation){ showError('Geolocation not supported'); return; }
    navigator.geolocation.getCurrentPosition(
      pos => fetchWeather({ lat: pos.coords.latitude, lon: pos.coords.longitude }),
      err => showError(err.message || 'Location failed'),
      { enableHighAccuracy:false, maximumAge: 60000, timeout: 8000 }
    );
  });

  // auto-focus
  cityInput.focus();

  // ---- Animated precipitation (rain / snow) ----
  const rainLayer = document.querySelector('.rain-layer');
  const snowLayer = document.querySelector('.snow-layer');
  let particleTimer;

  function clearParticles(){
    if (particleTimer) {
      clearInterval(particleTimer);
      particleTimer = null;
    }
    [rainLayer, snowLayer].forEach(layer => {
      if(!layer) return;
      while (layer.firstChild) layer.removeChild(layer.firstChild);
    });
  }

  function launchParticles(theme){
    clearParticles();
    if(theme === 'theme-rain'){
      particleTimer = setInterval(()=> spawnRaindrop(), 60);
    } else if(theme === 'theme-snow'){
      particleTimer = setInterval(()=> spawnSnowflake(), 180);
    }
  }

  function spawnRaindrop(){
    if(!rainLayer) return;
    const drop = document.createElement('div');
    drop.className = 'raindrop';
    drop.style.left = Math.random()*100 + 'vw';
    const dur = 900 + Math.random()*1100;
    drop.style.animationDuration = dur + 'ms';
    rainLayer.appendChild(drop);
    setTimeout(()=> { if(drop && drop.parentNode) drop.parentNode.removeChild(drop); }, dur + 600);
  }

  function spawnSnowflake(){
    if(!snowLayer) return;
    const flake = document.createElement('div');
    flake.className = 'snowflake';
    flake.style.left = Math.random()*100 + 'vw';
    const size = 4 + Math.random()*7;
    flake.style.width = size + 'px';
    flake.style.height = size + 'px';
    const dur = 3000 + Math.random()*4200;
    flake.style.animationDuration = dur + 'ms';
    snowLayer.appendChild(flake);
    setTimeout(()=> { if(flake && flake.parentNode) flake.parentNode.removeChild(flake); }, dur + 1000);
  }
})();
