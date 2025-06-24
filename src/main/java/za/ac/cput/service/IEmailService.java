package za.ac.cput.service;

import java.util.Map;

/**
 * Interface for the email sending service.
 * Defines methods for sending both simple and templated HTML emails.
 */
public interface IEmailService {

    /**
     * Sends a simple plain text email.
     *
     * @param to      The recipient's email address.
     * @param subject The subject of the email.
     * @param text    The plain text content of the email.
     */
    void sendSimpleMessage(String to, String subject, String text);

    /**
     * Sends an HTML email based on a Thymeleaf template.
     *
     * @param to           The recipient's email address.
     * @param subject      The subject of the email.
     * @param templateName The name of the Thymeleaf template file (without the .html extension).
     * @param context      A map of variables to be used within the template (e.g., "name" -> "John Doe").
     */
    void sendHtmlMessage(String to, String subject, String templateName, Map<String, Object> context);
}