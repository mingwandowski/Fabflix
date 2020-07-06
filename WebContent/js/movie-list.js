/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/*
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {

    $("#sortOrder").find("option[value='" + resultData['orderBy'] + "']").attr("selected",true);

    $("#resultLimit").find("option[value='" + resultData['numberOfList'] + "']").attr("selected",true);

    let currentPage = resultData['page'];
    let numOfData = resultData['numOfData'];
    let numberOfList = resultData['numberOfList'];

    let numOfPage = Math.floor(numOfData / numberOfList);

    if(currentPage == 0){
        $("#prev").attr('disabled',true);
    }
    if(currentPage == numOfPage){
        $("#next").attr('disabled',true);
    }
    let pageBodyElement = $("#page");
    pageBodyElement.append(parseInt(currentPage) + 1);
    pageBodyElement.append(" / ");
    pageBodyElement.append(parseInt(numOfPage) + 1);

    // Populate the movie table
    // Find the empty table body by id "movie_table_body"
    let movieTableBody = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 20 entries
    for (let i = 0; i < resultData["movieData"].length; i++) {

        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href=single-movie.html?id=' + resultData["movieData"][i]['movie_id'] + '>'
            + resultData["movieData"][i]["movie_title"] +     // display movie_title for the link text
            '</a>' +
            "</th>";
        rowHTML += "<th>" + resultData["movieData"][i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData["movieData"][i]["movie_director"] + "</th>";

        rowHTML += "<th>";
        for(let g = 0; g < Math.min(3, resultData["movieData"][i]["genres_name"].length); g++){
            rowHTML +=
                '<a href=movie-list.html?type=browse-genre&genreId='
                + resultData["movieData"][i]['genres_name'][g]['genre_id']
                + '&orderBy=rating%20desc,%20title%20asc&numberOfList=10&page=0>'
                + resultData["movieData"][i]["genres_name"][g]["genre_name"] + "<br>";
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        rowHTML += "</th>";

        rowHTML += "<th>";
        for(let s = 0; s < Math.min(3, resultData["movieData"][i]["stars_name"].length); s++){
            // Add a link to single-movie.html with id passed with GET url parameter
            rowHTML += '<a href=single-star.html?id=' + resultData["movieData"][i]["stars_name"][s]["star_id"] + '>'
            rowHTML += resultData["movieData"][i]["stars_name"][s]["star_name"] + "<br>";
        }
        rowHTML = rowHTML.substring(0, rowHTML.length - 4);
        rowHTML += "</th>";

        rowHTML += "<th>" + resultData["movieData"][i]["rating"] + "</th>";

        rowHTML +=
            "<th>" +

            '<a href=cart.html?method=add&id=' + resultData["movieData"][i]['movie_id'] + '>'
            + "Add to Cart" + '</a>'
            + "</th>";

        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBody.append(rowHTML);
    }
}

function redirectToMovieListPage(){
    window.location.replace("movie-list.html");
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

let type = getParameterByName('type');

let title = getParameterByName('title');
let year = getParameterByName('year');
let director = getParameterByName('director');
let starName = getParameterByName('starName');

let genreId = getParameterByName('genreId');
let firstLetter = getParameterByName('firstLetter');

let orderBy = getParameterByName('orderBy');
let numberOfList = getParameterByName('numberOfList');
let page = getParameterByName('page');

let url = "?type=" + type;
switch(type) {
    case 'adv-search':
        url += "&title=" + title + "&year=" + year + "&director=" + director + "&starName=" + starName;
        break;
    case 'browse-genre':
        url += "&genreId=" + genreId;
        break;
    case 'browse-title':
        url += "&firstLetter=" + firstLetter;
        break;
}
console.log(url);

$(" #sortOrder").change(function(){
    let options=$("#sortOrder");
    let value = options.val();
    // let text = options.text();
    // alert("value = " + value);
    // alert("text = " + text);
    window.location.replace("movie-list.html" + url + "&orderBy=" + value + "&numberOfList=" + numberOfList + "&page=" + page);
    // jQuery.ajax({
    //     dataType: "json", // Setting return data type
    //     method: "GET", // Setting request method
    //     url: "api/movie-list" + url + "&orderBy=" + value + "&numberOfList=" + numberOfList + "&page=" + page, // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
    //     success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
    // });
});

$(" #resultLimit").change(function(){
    let options=$("#resultLimit");
    let value = options.val();
    // let text = options.text();
    // alert("value = " + value);
    // alert("text = " + text);
    window.location.replace("movie-list.html" + url + "&orderBy=" + orderBy + "&numberOfList=" + value + "&page=" + page);
    // jQuery.ajax({
    //     dataType: "json", // Setting return data type
    //     method: "GET", // Setting request method
    //     url: "api/limit-change?numberOfList=" + value, // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
    //     success: redirectToMovieListPage // Setting callback function to handle data returned successfully by the StarsServlet
    // });
});

$('#prev').click(function(){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/page?page=prev", // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
        success: redirectToMovieListPage // Setting callback function to handle data returned successfully by the StarsServlet
    });
});

$('#next').click(function(){
    jQuery.ajax({
        dataType: "json", // Setting return data type
        method: "GET", // Setting request method
        url: "api/page?page=next", // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
        success: redirectToMovieListPage // Setting callback function to handle data returned successfully by the StarsServlet
    });
});

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie-list" + url + "&orderBy=" + orderBy + "&numberOfList=" + numberOfList + "&page=" + page, // Setting request url, which is mapped by StarsServlet in MoviesServlet.java
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});