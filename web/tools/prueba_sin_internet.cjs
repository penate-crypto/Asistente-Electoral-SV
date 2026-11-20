const { chromium, devices } = require('playwright');
(async () => {
  const b = await chromium.launch(); const c = await b.newContext({ ...devices['Pixel 7'], serviceWorkers: 'allow' }); const p = await c.newPage();
  await p.goto('http://localhost:8765/index.html'); await p.click('.splash');
  await p.fill('input[aria-label^="Usuario"]', '123456789'); await p.fill('input[aria-label="Contraseña"]', 'abc123'); await p.click('button[type=submit]'); await p.click('text=ACEPTAR Y CONTINUAR');
  await p.waitForSelector('.hero'); await p.evaluate(() => navigator.serviceWorker.ready); await p.waitForTimeout(1500);
  await p.reload(); await p.waitForSelector('.hero'); await p.waitForTimeout(800);
  await c.setOffline(true);
  await p.reload(); await p.waitForSelector('.hero', { timeout: 15000 });
  await p.fill('textarea', 'el votante no se parece a la foto del dui'); await p.click('.send'); await p.waitForSelector('.ans .blk.si');
  console.log('SIN INTERNET: la consulta se respondió →', (await p.textContent('.ans h2')).trim());
  await b.close();
})();
