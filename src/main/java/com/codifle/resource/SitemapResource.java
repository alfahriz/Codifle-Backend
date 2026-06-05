package com.codifle.resource;

import com.codifle.service.DbService;
import com.codifle.util.ApiResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;

@Path("/sitemap.xml")
public class SitemapResource {

    @Inject DbService db;

    @ConfigProperty(name = "codifle.site.base-url", defaultValue = "https://codifle.com")
    String baseUrl;

    @GET
    @Produces("application/xml")
    public Response get() {
        try {
            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");

            // Static pages
            appendUrl(xml, baseUrl + "/",          "1.0",  "weekly");
            appendUrl(xml, baseUrl + "/#about",    "0.8",  "monthly");
            appendUrl(xml, baseUrl + "/#services", "0.8",  "monthly");
            appendUrl(xml, baseUrl + "/#products", "0.8",  "monthly");
            appendUrl(xml, baseUrl + "/#clients",  "0.6",  "monthly");
            appendUrl(xml, baseUrl + "/#blog",     "0.7",  "weekly");
            appendUrl(xml, baseUrl + "/#contact",  "0.6",  "monthly");

            // Services
            List<Map<String, Object>> services = db.queryList("SELECT * FROM fn_get_services()");
            for (Map<String, Object> svc : services) {
                String key = String.valueOf(svc.get("key"));
                appendUrl(xml, baseUrl + "/services/" + key, "0.7", "monthly");
            }

            // Products
            List<Map<String, Object>> products = db.queryList("SELECT * FROM fn_get_products()");
            for (Map<String, Object> prod : products) {
                String id = String.valueOf(prod.get("id"));
                appendUrl(xml, baseUrl + "/products/" + id, "0.7", "monthly");
            }

            // Articles
            List<Map<String, Object>> articles = db.queryList("SELECT * FROM fn_get_articles(1000, 0)");
            for (Map<String, Object> article : articles) {
                String slug = String.valueOf(article.get("slug"));
                appendUrl(xml, baseUrl + "/blog/" + slug, "0.6", "monthly");
            }

            xml.append("</urlset>");
            return Response.ok(xml.toString()).build();
        } catch (Exception e) {
            return ApiResponse.serverError(e);
        }
    }

    private void appendUrl(StringBuilder xml, String loc, String priority, String changefreq) {
        xml.append("  <url>\n");
        xml.append("    <loc>").append(escapeXml(loc)).append("</loc>\n");
        xml.append("    <changefreq>").append(changefreq).append("</changefreq>\n");
        xml.append("    <priority>").append(priority).append("</priority>\n");
        xml.append("  </url>\n");
    }

    private String escapeXml(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
