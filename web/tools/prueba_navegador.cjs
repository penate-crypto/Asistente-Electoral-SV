const { chromium, devices } = require('playwright');
const SHOTS = process.argv[2]; const URL = process.argv[3] || 'http://127.0.0.1:8765/index.html';
(async () => {
  const b = await chromium.launch();
  const ctx = await b.newContext({ ...devices['iPhone 13'], locale: 'es-SV' });
  const p = await ctx.newPage();
  const errores = [];
  p.on('console', m => { if (m.type() === 'error') errores.push('console: ' + m.text()); });
  p.on('pageerror', e => errores.push('pageerror: ' + e.message));
  p.on('requestfailed', r => errores.push('requestfailed: ' + r.url()));
  const paso = async (nombre, fn) => { try { await fn(); const raro = await p.evaluate(() => (document.body.innerText.match(/\[object |\bundefined\b|\bNaN\b|\bnull\b/g) || []).join(',')); if (raro) throw new Error('texto raro en pantalla: ' + raro); console.log('OK   ' + nombre); } catch (e) { console.log('FALLA ' + nombre + ' -> ' + e.message.split('\n')[0]); } };
  await p.goto(URL);
  await paso('intro', async () => { await p.waitForSelector('.splash img'); await p.screenshot({ path: SHOTS + '/01_intro.png' }); await p.click('.splash'); });
  await paso('login (validaciones)', async () => {
    await p.waitForSelector('form.box');
    await p.fill('input[aria-label^="Usuario"]', '123'); await p.fill('input[aria-label="Contraseña"]', 'abc123'); await p.click('button[type=submit]');
    await p.waitForSelector('.err:not(:empty)');
    await p.fill('input[aria-label^="Usuario"]', '123456789'); await p.screenshot({ path: SHOTS + '/02_login.png' });
    await p.click('button[type=submit]');
  });
  await paso('consentimiento legal', async () => { await p.waitForSelector('text=Consentimiento Requerido'); await p.screenshot({ path: SHOTS + '/03_legal.png' }); await p.click('text=ACEPTAR Y CONTINUAR'); });
  await paso('inicio del chat', async () => { await p.waitForSelector('.hero'); await p.screenshot({ path: SHOTS + '/04_chat_inicio.png' }); });
  await paso('consulta de texto (DUI no aparece en padrón)', async () => {
    await p.fill('textarea', 'persona que fue a votar no aparece en el padron'); await p.click('.send');
    await p.waitForSelector('.ans .blk.si'); const t = await p.textContent('.ans'); if (!/padr[oó]n/i.test(t)) throw new Error('sin contenido');
    await p.screenshot({ path: SHOTS + '/05_chat_respuesta.png' }); await p.screenshot({ path: SHOTS + '/05b_chat_respuesta_completa.png', fullPage: false });
  });
  await paso('botón VER FUENTE abre el PDF en la página', async () => {
    await p.click('.fuente button >> nth=0'); await p.waitForSelector('.pdfview canvas', { timeout: 20000 });
    const info = await p.inputValue('.pdffoot input'); console.log('     página abierta:', info); await p.waitForTimeout(500); await p.screenshot({ path: SHOTS + '/06_pdf.png' });
  });
  await paso('buscador del libro (artículo 198)', async () => {
    await p.click('.pdfbar button:has-text("Buscar")'); await p.fill('.modal input', '198'); await p.waitForSelector('.modal .res button', { timeout: 8000 });
    await p.screenshot({ path: SHOTS + '/07_pdf_buscar.png' }); await p.click('.modal .res button >> nth=0'); await p.waitForTimeout(800);
    console.log('     salta a página:', await p.inputValue('.pdffoot input')); await p.click('button[aria-label="Cerrar visor PDF"]');
  });
  await paso('consulta ambigua muestra opciones', async () => { await p.fill('textarea', 'roles y requisitos de la jrv'); await p.click('.send'); await p.waitForSelector('.ans'); await p.waitForTimeout(300); const n = await p.locator('.ans .opt').count(); console.log('     opciones mostradas:', n); if (n < 2) throw new Error('sin opciones'); await p.click('.ans .opt >> nth=0'); await p.waitForSelector('.ans .blk.si'); });
  await paso('pregunta fuera de tema se rechaza', async () => {
    await p.fill('textarea', 'quien gano el partido de futbol ayer'); await p.click('.send'); await p.waitForSelector('text=AVISO DE CONSULTA');
  });
  await paso('biblioteca', async () => { await p.click('.bottomnav button[data-r=biblioteca]'); await p.waitForSelector('.docrow'); await p.screenshot({ path: SHOTS + '/08_biblioteca.png' }); });
  await paso('paso a paso', async () => { await p.click('.bottomnav button[data-r=paso]'); await p.waitForSelector('details.stage'); await p.click('.chip:has-text("Secretario")'); await p.screenshot({ path: SHOTS + '/09_paso.png' }); });
  await paso('casos y soluciones', async () => { await p.click('.bottomnav button[data-r=test]'); await p.waitForSelector('text=Ver Caso y Solución'); await p.screenshot({ path: SHOTS + '/10_casos.png' }); await p.click('text=Ver Caso y Solución >> nth=0'); await p.waitForSelector('text=SOLUCIÓN CORRECTA'); await p.screenshot({ path: SHOTS + '/11_caso_detalle.png' }); });
  await paso('examen de 25 preguntas completo', async () => {
    await p.click('.bottomnav button[data-r=test]'); await p.click('.tabs button:has-text("Examen")'); await p.click('text=Comenzar examen'); await p.waitForSelector('.opt');
    for (let i = 0; i < 25; i++) { await p.click('.opt >> nth=1'); if (i < 24) await p.click('.row2 button:has-text("Siguiente ▶")'); }
    await p.screenshot({ path: SHOTS + '/12_examen.png' }); await p.click('.row2 button:has-text("Finalizar examen")'); await p.waitForSelector('.score');
    const sc = await p.textContent('.score'); console.log('     calificación (todo B):', sc); await p.screenshot({ path: SHOTS + '/13_resultado.png' });
  });
  await paso('test de 125 con corrección inmediata + papeleta', async () => {
    await p.click('.bottomnav button[data-r=config]'); await p.click('text=TEST — ABRIR 125 PREGUNTAS'); await p.waitForSelector('.opt');
    await p.click('.grid button >> nth=3'); await p.waitForSelector('.ballot'); await p.click('.opt >> nth=0'); await p.waitForSelector('.blk.si, .blk.no'); await p.screenshot({ path: SHOTS + '/14_test125_papeleta.png' });
    await p.click('text=VER FUENTE'); await p.waitForSelector('.pdfview canvas', { timeout: 20000 }); console.log('     fuente de pregunta en página:', await p.inputValue('.pdffoot input')); await p.click('button[aria-label="Cerrar visor PDF"]');
  });
  await paso('capacitación → preguntar', async () => { await p.click('button[aria-label="Abrir menú"]'); await p.click('.drawer .navitem:has-text("Capacitación")'); await p.waitForSelector('text=Preguntar al asistente'); await p.screenshot({ path: SHOTS + '/15_capacitacion.png' }); await p.click('text=Preguntar al asistente >> nth=2'); await p.waitForSelector('.ans'); });
  await paso('menú lateral con historial', async () => { await p.click('button[aria-label="Abrir menú"]'); await p.waitForSelector('.histitem'); await p.screenshot({ path: SHOTS + '/16_menu.png' }); await p.click('.drawer-bg', { position: { x: 380, y: 400 } }); });
  await paso('modo oscuro', async () => { await p.click('button[aria-label^="Cambiar tema"]'); await p.screenshot({ path: SHOTS + '/17_oscuro.png' }); });
  await paso('ajustes', async () => { await p.click('.bottomnav button[data-r=config]'); await p.waitForSelector('text=Configuración'); await p.screenshot({ path: SHOTS + '/18_ajustes.png' }); });
  console.log('\nERRORES DE CONSOLA/RED:', errores.length); errores.slice(0, 15).forEach(e => console.log('  ' + e));
  await b.close();
})();
