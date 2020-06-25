let advSearch = jQuery("#adv-search-form");



// let advanceSearch = $("#advance_search_form");

function handleSearchResult(resultDataString) {
    console.log(resultDataString);
    // let resultDataJson = JSON.parse(resultDataString);
    let resultDataJson = resultDataString;
    console.log("handle search response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);
    // if (resultDataJson["status"] === "success") {
    //     window.location.replace("movie-list.html");
    // }
    window.location.replace("movie-list.html");

}

function handleSearchInfo(searchEvent) {
    // console.log("submit search form");
    // const url = "api/adv-search";
    // console.log("api/adv-search");
    // console.log(url);
    let url = "movie-list.html?type=adv-search&";
    url += advSearch.serialize();
    url += "&orderBy=rating desc, title asc&numberOfList=10&page=0";
    window.location.replace(url);
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    searchEvent.preventDefault();

    $.ajax("api/adv-search", {
        method: "GET",
        data: advSearch.serialize(),
        success: handleSearchResult
    });
}

advSearch.submit(handleSearchInfo);