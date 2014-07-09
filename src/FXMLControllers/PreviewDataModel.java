package FXMLControllers;

/**
 * Created by nordgaimer on 07.07.14.
 */
public class PreviewDataModel {

    private String docID;
    private String docName;


    public PreviewDataModel(String docID, String docName) {
        this.docID = docID;
        this.docName = docName;
    }

    public PreviewDataModel() {
    }


    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

}

