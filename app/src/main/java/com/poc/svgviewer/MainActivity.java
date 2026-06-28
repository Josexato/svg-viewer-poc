package com.poc.svgviewer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private static final int REQUEST_OPEN_SVG = 1;

    private WebView webView;

    // SVG de ejemplo que se muestra al abrir, antes de elegir un archivo.
    private static final String SAMPLE_SVG =
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
          + "        font-size='22' font-weight='bold' fill='#ffffff'>Toca “Abrir SVG”</text>"
          + "</svg>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        Button openButton = new Button(this);
        openButton.setText("Abrir SVG…");
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSvgPicker();
            }
        });
        root.addView(openButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        webView = new WebView(this);
        WebSettings settings = webView.getSettings();
        settings.setBuiltInZoomControls(true);   // pellizcar para zoom
        settings.setDisplayZoomControls(false);  // sin botones +/-
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        // El WebView ocupa todo el espacio restante (weight = 1).
        root.addView(webView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));

        render(SAMPLE_SVG);
        setContentView(root);
    }

    // Abre el selector de archivos del sistema (sin permisos en runtime).
    private void openSvgPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // algunos .svg se reportan como octet-stream
        try {
            startActivityForResult(intent, REQUEST_OPEN_SVG);
        } catch (Exception e) {
            Toast.makeText(this, "No hay app de archivos disponible", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_OPEN_SVG && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String svg = readText(uri);
                if (svg != null && svg.contains("<svg")) {
                    render(svg);
                } else {
                    Toast.makeText(this, "El archivo no parece un SVG", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String readText(Uri uri) {
        try (InputStream in = getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(in, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        } catch (Exception e) {
            Toast.makeText(this, "No se pudo leer el archivo", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    // Envuelve el SVG en una página HTML mínima y lo carga en el WebView.
    private void render(String svg) {
        // Quitamos prólogo XML y DOCTYPE: estorban al incrustar SVG inline en HTML.
        String cleaned = svg
                .replaceFirst("(?s)^\\s*<\\?xml.*?\\?>", "")
                .replaceFirst("(?s)<!DOCTYPE[^>]*>", "");

        String html =
                "<!DOCTYPE html><html><head>"
              + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
              + "<style>html,body{margin:0;height:100%;background:#fafafa;}"
              + "body{display:flex;align-items:center;justify-content:center;}"
              + "svg{width:100%;height:auto;max-width:100%;}</style>"
              + "</head><body>" + cleaned + "</body></html>";

        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);
    }
}
