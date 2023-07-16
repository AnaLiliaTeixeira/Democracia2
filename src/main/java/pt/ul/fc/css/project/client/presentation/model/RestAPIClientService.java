package pt.ul.fc.css.project.client.presentation.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import pt.ul.fc.css.project.server.business.services.dtos.CitizenDTO;
import pt.ul.fc.css.project.server.business.services.dtos.LawProjectDTO;

public class RestAPIClientService {

    private static final String URL = "http://localhost:8080/api";

    private static RestAPIClientService instance;

    private RestAPIClientService() {}

    public static RestAPIClientService getInstance() {
        if (instance == null)
            instance = new RestAPIClientService();
        return instance;
    }

    public LawProjectDTO getLawProject(Long id) throws IOException {
    String url = URL + "/lawprojects/" + id;
    String jsonResponse = sendGetRequest(url);
    return parseLawProject(jsonResponse);
    }

    public List<LawProjectDTO> getAllNonExpiredLawProjects() throws IOException {
        String url = URL + "/lawprojects/nonexpired";
        String jsonResponse = sendGetRequest(url);
        return parseLawProjects(jsonResponse);
    }

    public List<LawProjectDTO> getCurrentVotations() throws IOException {
        String url = URL + "/lawprojects_votation";
        String jsonResponse = sendGetRequest(url);
        return parseLawProjects(jsonResponse);
    }

    public String signLawProject(Long lawProjectId, Long citizenId) throws IOException {
        String url = URL + "/" + citizenId + "/lawproject/sign/" + lawProjectId;
        return sendPostRequest(url, null);
    }

    public String voteLawProject(Long lawProjectId, Long citizenId, String vote) throws IOException {
        String url = URL + "/" + citizenId + "/vote/" + lawProjectId;
        String requestBody = String.valueOf(vote);
        return sendPostRequest(url, requestBody);
    }

    public CitizenDTO getCitizen(Long id) throws IOException {
        String url = URL + "/citizens/" + id;
        String jsonResponse = sendGetRequest(url);
        return parseCitizen(jsonResponse);
    }

    private List<LawProjectDTO> parseLawProjects(String jsonResponse) throws IOException{
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Register the JavaTimeModule for LocalDateTime support
            return objectMapper.readValue(jsonResponse, new TypeReference<List<LawProjectDTO>>() {});
        } catch (IOException e) {
            throw new IOException("Failed to get Law Projects.");
        }
    }

    private LawProjectDTO parseLawProject(String jsonResponse) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            return objectMapper.readValue(jsonResponse, LawProjectDTO.class);
        } catch (IOException e) {
            throw new IOException("Failed to get Law Project.");
        }
    }

    private CitizenDTO parseCitizen(String jsonResponse) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(jsonResponse, CitizenDTO.class);
        } catch (IOException e) {
            throw new IOException("Failed to get citizen.");
        }
    }

    private String sendGetRequest(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            return response.toString();
        } else {
            throw new IOException("GET request failed with response code: " + responseCode);
        }
    }

    private String sendPostRequest(String urlString, String requestBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (requestBody != null && !requestBody.isEmpty()) {
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();
            outputStream.close();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close();
            return sb.toString();
        } else {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder errorSb = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorSb.append(errorLine);
            }
            errorReader.close();
            throw new IOException(errorSb.toString());
        }
    }

}
