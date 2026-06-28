package com.poc.svgviewer;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;

public class MainActivity extends Activity {

    // SVG de ejemplo incrustado. Sustituye este bloque por la salida de
    // AlmaGag (u otro SVG) para visualizar el tuyo. Se usan comillas simples
    // en los atributos para no tener que escaparlas dentro del String Java.
    private static final String SVG =
            "<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 300'>"
          + "  <defs>"
          + "    <linearGradient id='g' x1='0' y1='0' x2='1' y2='1'>"
          + "      <stop offset='0%' stop-color='#e53935'/>"
          + "      <stop offset='100%' stop-color='#1e88e5'/>"
          + "    </linearGradient>"
          + "  </defs>"
          + "  <rect x='10' y='10' width='380' height='280' rx='20' fill='url(#g)'/>"
          + "  <circle cx='200' cy='130' r='70' fill='#ffffff' opacity='0.85'/>"
          + "  <path d='M165 130 L195 160 L240 105' stroke='#1e88e5' stroke-width='12'"
          + "        fill='none' stroke-linecap='round' stroke-linejoin='round'/>"
          + "  <text x='200' y='240' text-anchor='middle' font-family='sans-serif'"
          + "        font-size='26' font-weight='bold' fill='#ffffff'>SVG Viewer POC</text>"
          + "</svg>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = new WebView(this);

        WebSettings settings = webView.getSettings();
        // Pellizcar para hacer zoom, sin los botones +/- en pantalla.
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        // Ajustar el contenido al ancho de la pantalla al abrir.
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // Envolvemos el SVG en una página HTML mínima para que el motor del
        // navegador lo dibuje y lo escale al ancho del dispositivo.
        String html =
                "<!DOCTYPE html><html><head>"
              + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
              + "<style>html,body{margin:0;height:100%;background:#fafafa;}"
              + "body{display:flex;align-items:center;justify-content:center;}"
              + "svg{width:100%;height:auto;max-width:100%;}</style>"
              + "</head><body>" + SVG + "</body></html>";

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        setContentView(webView);
    }
}
