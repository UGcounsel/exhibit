package Exhibition.model;

public class Participant {
    private String registrationId;
    private String name;
    private String faculty;
    private String projectTitle;
    private String contact;
    private String email;
    private String imagePath;

    public Participant(String registrationId, String name, String faculty, String projectTitle, String contact, String email, String imagePath) {
        this.registrationId = registrationId;
        this.name = name;
        this.faculty = faculty;
        this.projectTitle = projectTitle;
        this.contact = contact;
        this.email = email;
        this.imagePath = imagePath;
    }

    // Getters
    public String getRegistrationId() { return registrationId; }
    public String getName() { return name; }
    public String getFaculty() { return faculty; }
    public String getProjectTitle() { return projectTitle; }
    public String getContact() { return contact; }
    public String getEmail() { return email; }
    public String getImagePath() { return imagePath; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", registrationId, name, faculty);
    }
}
