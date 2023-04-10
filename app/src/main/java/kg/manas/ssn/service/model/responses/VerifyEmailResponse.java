package kg.manas.ssn.service.model.responses;

public class VerifyEmailResponse {

    private boolean isVerified;

    public VerifyEmailResponse(boolean isVerified) {
        this.isVerified = isVerified;
    }

    public boolean isVerified() {
        return isVerified;
    }

}
