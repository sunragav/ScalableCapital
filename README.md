# ScalableCapital
An App that displays github repos

[![preview](https://imgur.com/1HLkjSb.jpg)](https://webm.red/view/eAQu.webm)

For the sake of simplicity, only the default_branch of a repo is considered for displaying the commits and calculating the per commits count.

Following properties in the gradle.properties can be manipulated.

* **debugOwner**=mralexgray

* **prodOwner**=mralexgray

* **debugAuth**=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

* **prodAuth**="ENTER YOUR GITHUB PERSONALIZED ACCESS TOKEN"
  
* **authPrefix**=token

In case of network error, api error or an empty result for the api request, a custom empty view is displayed.

The empty view shows a sequence of jokes on git and software development in general.

You may provide wrong values here to test whether the error states are handled in the app.

Also one can test the app in flight mode to see the behavior in the absence of network.

Usage of each property in the gradle.properties:

- Use "debugAuth" for providing your own GITHUB personal access token, if any.

There is limit imposed by Github on the number of calls made without authorization. 
  
By using a personal access token, we are allowed to make up to **5000** calls per hour.

To learn how to create a personal access token, please refer this [page](https://docs.github.com/en/github/authenticating-to-github/keeping-your-account-and-data-secure/creating-a-personal-access-token).

- Use **debugOwner** for providing your GITHUB handle(username).

prodAuth and prodOwner does the same thing for the release flavor.

- Use **debugBaseUrl** for providing the base url of the API
You can replace it with a mock server url, just in case, if you want to.
  
prodBaseUrl is used for release flavor.
  
eg.
**prodBaseUrl**=https://api.github.com/
  
**debugBaseUrl**=https://7ff3d38f-cc5f-4625-a9e6-b8024bfdf7d2.mock.pstmn.io
  
Postman mockserver except for graphql, mocks the rest calls for repos and commits list view


The project uses a simple MVVM architecture using the jetpack components.
  
The project is a single gradle android module.
  
Due to the time constraint, I have not added any tests or complex architecture layers. 
  
The project does not use any local db.

The paging is handled using the **paging library 3.0**

App states are maintained in the **Jetpack component's ViewModel**, so that they survive the configuration changes out of the box.

The dependency injection is achieved using **dagger 2.0**
  
Service calls are made using the retrofit library.
  
As Apollo graphql library is not mentioned in the Test assignment description, I have used retrofit to call the graphql calls as well.

There are 3 graphql queries used in the CommitsCountHelper:

1. Query 1 used for fetching the most recent commit cursor hash, it's date and the totalNumber of commits in a repo.

```javascript
query {
     repository(owner:"%s", name:"%s") {
       object(expression:"%s") {
         ... on Commit {
           history(first:1) {
             nodes {
               committedDate
              }
             totalCount
             pageInfo {
               endCursor
             }
           }
         }
       }
     }
   }
```

2. Query 2 used for fetching the date of the very first commit of the repo.

```javascript
query {
     repository(owner:"%s", name:"%s") {
       object(expression:"%s") {
         ... on Commit {
           history(first:1,after: "%s %d") {
             nodes {
               committedDate
              }
             totalCount
             pageInfo {
               endCursor
             }
           }
         }
       }
     }
   }
 ```

3. Query 3 is used to fetch the commits count per month

```javascript
query {
     repository(owner:"%s", name:"%s") {
       object(expression:"%s") {
         ... on Commit {
           history(since:"%s",until:"%s") {
             totalCount
           }
         }
       }
     }
   }
```

The dates of the first and last commits in a repo are fetched and a list of months in between  
those dates are calculated. The date ranges for each month are used to query the commits count per month.
Date range for the list of months per year is calculated in the DateRange class.

For example, for a repo, if the first commit date is "2015-12-21T00:00:00Z" and the last commit date is "2016-2-12T00:00:00Z"

The following date ranges are created for each month in between them.

1. "2015-12-01T00:00:00Z" to "2015-12-31T23:59:99Z" for Dec 2015

2. "2016-01-01T00:00:00Z" to "2016-01-31T23:59:99Z" for Jan 2016

3. "2016-02-01T00:00:00Z" to "2016-02-29T23:59:99Z" for Feb 2016

Then 3 individual graphql calls are made to fetch the commits count for the individual months.

The max of the per month commits is calculated from this list of commits count.

A custom view has been created to show the commits count per month as given in the spec.

The custom view is implemented in the CommitsCountView.

It loads a progress till the graphql calls are made and the per month commits count and the maximum per month commits count are calculated.

Once the count is ready it is notified via a livedata in the CommitsViewModel.

The height of the image view in the CommitsCountView is manipulated to display the commits count per month visually.

The height of the image view is calculated using the ratio of the commits count for the current month to the maximum commits count per month.

```java
val ratio = commitsCount.toFloat() / maxCommitCount

height = (binding.root.height * (1.0 - imageHeightPercentage) * ratio).toInt()
```

The **imageHeightPercentage** is the percentage of the height of the image view with respect to the custom view's height.

**binding.root** refers to the custom view's roots layout.

The CommitsCountView displays the commits per month, year and the month for a period of 1.5 secs.

After 1.5 secs the same details for the next month are shown.

The image view height gets animated from old ratio to the new value as specified in the description.

Other than the graph ql api which is used for calling 3 different queries,

there are 2 rest api calls used.

1. Rest Api call 1 for fetching the repositories list for a given username

2. Rest Api call 2 for fetching the commits list for the selected repo

```java
interface RepoService {
    @GET("users/{owner}/repos")
    suspend fun getRepos(
        @Path("owner") owner: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<RepoResponse>>

    @GET("repos/{owner}/{name}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("name") name: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Response<List<CommitResponse>>

    @Headers("Content-Type: application/json")
    @POST("graphql")
    suspend fun postDynamicQuery(
        @Body body: String
    ): Response<CommitsResponse>
}
```
There are 3 view models(ViewModel from jetpack components) used.

1. Activity scoped **HomeViewModel** which has a livedata to notify the title change of the ActionBar on entering the commits fragment.

2. **CommitsFragment** scoped **CommitsViewModel** exposes 1 mutable live data and 2 livedatas for subscription.

```java
@GET("repos/{owner}/{name}/commits")
   suspend fun getCommits(
       @Path("owner") owner: String,
       @Path("name") name: String,
       @Query("page") page: Int,
       @Query("per_page") perPage: Int
   ): Response<List<CommitResponse>>
```

1 mutable livedata for notify the repo name selected in the repos fragment

2 livedatas, one for notifying the commit list adapter of the new page loaded, once the commits REST api call succeeds, and

another for notifying the CommitsCountView once the processing of the commits count per month is complete.

3. **ReposFragment** scoped **ReposViewModel** exposes 1 livedata for notifying the repos list adapter of the page loaded, once the repos REST api call succeeds

```java
@GET("users/{owner}/repos")
   suspend fun getRepos(
       @Path("owner") owner: String,
       @Query("page") page: Int,
       @Query("per_page") perPage: Int
   ): Response<List<RepoResponse>>
```

The project's package structure is divided in to 2 features,

1. **commits**
2. **repos**

Each **feature** has a fragment and the following packages:

1. **repository**
2. **presentation**
3. **adapter**

The **commits** package additionally has customViews package which contains implementation for the
EmptyView and the CommitsCountView.

Other than these features the following packages exist:

1. **app**
2. **core**
3. **repository**

**app** package contains **di** package which has the dependency injections required.

The dagger component injects the dependencies from the following modules exposed:

1. **DataModule**, provides the values from the BuildConfig defined in the gradle.properties
2. **AppModule**, which provides utilities for injection in to the Fragments
3. **NetworkModule**, which provides the moshi json adapter, retrofit and okhttp dependencies of making the network calls
4. **PresentationModule**, which provides the view model factories for the **CommitsViewModel** and the **ReposViewModel**

These modules can be mocked during testing.

**core** package contains reusable base classes and util classes used across the features.

**repository** package contains the Network api's defined in the Retrofit understandable interface format,

and **ApiInterceptor** for injecting the **Authorization** header for each call.
