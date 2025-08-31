package com.cabbooking.service;

public interface IEmailService {

    /**
     * Sends a simple email to the specified recipient.
     *
     * @param to      the recipient's email address
     * @param subject the subject of the email
     * @param text    the body of the email
     */
    public void sendSimpleEmail(String to, String subject, String text);
}
