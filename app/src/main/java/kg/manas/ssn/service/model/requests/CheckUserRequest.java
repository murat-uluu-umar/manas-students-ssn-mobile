package kg.manas.ssn.service.model.requests;

public class CheckUserRequest {

    private String studentNumber;
    private String studentPassword;

    public CheckUserRequest(String studentNumber, String studentPassword) {
        this.studentNumber = studentNumber;
        this.studentPassword = studentPassword;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

}
