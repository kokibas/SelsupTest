package dto;

public class CreateDocumentRequest {
    private String document;
    private String signature;

    public CreateDocumentRequest(String document, String signature) {
        this.document = document;
        this.signature = signature;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public String toString() {
        return "CreateDocumentRequest{" +
                "document='" + document + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
