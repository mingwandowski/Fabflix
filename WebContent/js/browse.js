/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    let browseGenre = jQuery("#browse-genre");
    let browseAbc = jQuery("#browse-abc");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
        let genreHTML = "";
        genreHTML +=
            // Add a link to single-star.html with id passed with GET url parameter
            '<a class="dropdown-item" ' +
            'href="movie-list.html?type=browse-genre&genreId=' + resultData[i]['genre_id'] +
            '&orderBy=rating desc, title asc&numberOfList=10&page=0">'
            + resultData[i]["genre_name"] +     // display star_name for the link text
            '</a>';

        // Append the row created to the table body, which will refresh the page
        browseGenre.append(genreHTML);
    }

    for (let i = 65; i <= 90; i++) {
        let abcHTML = "";
        let tmp = String.fromCharCode(i);
        abcHTML +=
            // Add a link to single-star.html with id passed with GET url parameter
            '<a class="dropdown-item" ' +
            'href="movie-list.html?type=browse-title&firstLetter=' + tmp +
            '&orderBy=rating desc, title asc&numberOfList=10&page=0">'
            + tmp +     // display star_name for the link text
            '</a>';

        browseAbc.append(abcHTML);
    }
    //
    // for(let i = 0; i <= 9; i++){
    //     rowHTML +=
    //         // Add a link to single-star.html with id passed with GET url parameter
    //         '<a href="browse.html?first-later=' + i + '">'
    //         + i +     // display star_name for the link text
    //         '</a>';
    //     // 0 1 2 3 4 5 6 7 8 9
    //     if(i == 9){
    //         rowHTML += "<br>";
    //     }else{
    //         rowHTML += " | ";
    //     }
    // }
    // rowHTML += '<a href="browse.html?first-later=*">'
    //     + " * " +     // display star_name for the link text
    //     '</a>';
    // // Append the row created to the table body, which will refresh the page
    // browseTitleBodyElement.append(rowHTML);
}


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/browse", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});