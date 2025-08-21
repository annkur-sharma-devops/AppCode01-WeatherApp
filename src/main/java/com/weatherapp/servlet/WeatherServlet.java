package com.example.weatherapp.servlet;

import com.example.weatherapp.model.WeatherData;
import com.example.weatherapp.service.WeatherService;
import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * GET /weather?city=London
 * GET /weather?lat=...&lon=...
 * If ajax=true, returns JSON; otherwise forwards to index.jsp with attributes.
 */
public class WeatherServlet extends HttpServlet {
    private final WeatherService service = new WeatherService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ajax = req.getParameter("ajax");
        String city = req.getParameter("city");
        String lat = req.getParameter("lat");
        String lon = req.getParameter("lon");

        try {
            WeatherData data;
            if (lat != null && lon != null && !lat.isEmpty() && !lon.isEmpty()) {
                data = service.getByLatLon(Double.parseDouble(lat), Double.parseDouble(lon));
            } else if (city != null && !city.trim().isEmpty()) {
                data = service.getByCity(city.trim());
            } else {
                throw new IllegalArgumentException("Provide ?city=NAME or ?lat=&lon=");
            }

            if ("true".equalsIgnoreCase(ajax)) {
                resp.setContentType("application/json;charset=UTF-8");
                try (PrintWriter out = resp.getWriter()) {
                    out.write(gson.toJson(data));
                }
            } else {
                req.setAttribute("weather", data);
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp");
                rd.forward(req, resp);
            }
        } catch (Exception ex) {
            if ("true".equalsIgnoreCase(ajax)) {
                resp.setStatus(500);
                resp.setContentType("application/json;charset=UTF-8");
                try (PrintWriter out = resp.getWriter()) {
                    out.write("{\"error\":\"" + escape(ex.getMessage()) + "\"}");
                }
            } else {
                req.setAttribute("error", ex.getMessage());
                RequestDispatcher rd = req.getRequestDispatcher("/index.jsp");
                rd.forward(req, resp);
            }
        }
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\"","\\\"");
    }
}
