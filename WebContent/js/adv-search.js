let advSearch = jQuery("#adv-search-form");


function handleSearchInfo(searchEvent) {
    let url = "movie-list.html?type=adv-search&";
    url += advSearch.serialize();
    url += "&orderBy=rating desc, title asc&numberOfList=10&page=0";
    window.location.replace(url);

    searchEvent.preventDefault();
}

advSearch.submit(handleSearchInfo);