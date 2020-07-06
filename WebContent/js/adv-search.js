let advSearch = jQuery("#adv-search-form");


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

    // $.ajax("api/adv-search", {
    //     method: "GET",
    //     data: advSearch.serialize(),
    //     success: handleSearchInfo
    // });
}

advSearch.submit(handleSearchInfo);