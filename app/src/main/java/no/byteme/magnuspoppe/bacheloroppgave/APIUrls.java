package no.byteme.magnuspoppe.bacheloroppgave;

/**
 * Created by MagnusPoppe on 09/02/2017.
 */

public interface APIUrls
{
    // BASE URL:
    String BYTEME_API   = "http://byteme.no/api/";
    String BYTEME_DOCUMENTS = "http://byteme.no/document/";

    // DIFFERENT HTTP REQUESTS:
    String DIARY_GET    = "diary/get.php";  // GET POSTS
    String DIARY_POST   = "diary/post.php"; // CREATE NEW POST
    String DIARY_PUT    = "diary/put.php";  // EDIT A POST
}
