package org.oppia.android.scripts.common

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.oppia.android.scripts.common.model.GitHubIssue
import org.oppia.android.scripts.common.remote.GitHubService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.io.IOException

// TODO: finish documentation once bg dispatcher is incorporated.
/**
 * General utility for interfacing with a remote GitHub repository (specifically Oppia Android).
 *
 * Note that this utility expects 'gh' to be available in the local environment and be properly
 * authenticated.
 */
class GitHubClient(
  private val rootDirectory: File,
  private val dispatcher: CoroutineDispatcher,
  private val commandExecutor: CommandExecutor = CommandExecutorImpl(),
  private val repoOwner: String = "oppia",
  private val repoName: String = "oppia-android"
) {
  private val remoteApiUrl by lazy { "https://api.github.com/" }
  private val okHttpClient by lazy { OkHttpClient.Builder().build() }
  private val moshi by lazy { Moshi.Builder().build() }
  private val retrofit by lazy {
    Retrofit.Builder()
      .baseUrl(remoteApiUrl)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .client(okHttpClient)
      .build()
  }
  private val gitHubService by lazy { retrofit.create(GitHubService::class.java) }
  private val authorizationBearer by lazy { "Bearer ${retrieveAccessToken()}" }

  /**
   * Asynchronously returns all [GitHubIssue]s currently open in the Oppia Android GitHub project.
   */
  fun fetchAllOpenIssuesAsync(): Deferred<List<GitHubIssue>> {
    return CoroutineScope(dispatcher).async {
      // Fetch issues one page at a time (starting at page 1) until all are found.
      fetchOpenIssuesRecursive(startPageNumber = 1)
    }
  }

  private suspend fun fetchOpenIssuesRecursive(startPageNumber: Int): List<GitHubIssue> {
    val issues = fetchOpenIssues(startPageNumber).await()
    return if (issues.isNotEmpty()) {
      issues + fetchOpenIssuesRecursive(startPageNumber + 1)
    } else issues
  }

  private fun fetchOpenIssues(pageNumber: Int): Deferred<List<GitHubIssue>> {
    return CoroutineScope(dispatcher).async {
      val call = gitHubService.fetchOpenIssues(repoOwner, repoName, authorizationBearer, pageNumber)
      // Deferred blocking I/O operation to the dedicated I/O dispatcher.
      val response = withContext(Dispatchers.IO) { call.execute() }
      check(response.isSuccessful()) {
        "Failed to fetch issues at page $pageNumber: ${response.code()}\n${call.request()}" +
          "\n${response.errorBody()}."
      }
      return@async checkNotNull(response.body()) {
        "No issues response from GitHub for page: $pageNumber."
      }
    }
  }

  private fun retrieveAccessToken(): String {
    // First, make sure the command actually exists.
    try {
      commandExecutor.executeCommand(rootDirectory, "gh", "help")
    } catch (e: IOException) {
      throw IllegalStateException(
        "Failed to interact with gh tool. Please make sure your environment is set up properly" +
          " per https://github.com/oppia/oppia-android/wiki/Static-Analysis-Checks" +
          "#todo-open-checks.",
        e
      )
    }

    // Retrieve the access token that 'gh' is configured to use (to allow the script to run without
    // being tied to a specific access token).
    return commandExecutor.executeCommand(rootDirectory, "gh", "auth", "token").also {
      check(it.exitCode == 0) {
        "Failed to retrieve auth token from GH tool. Please make sure your environment is set up" +
          " properly per https://github.com/oppia/oppia-android/wiki/Static-Analysis-Checks" +
          "#todo-open-checks. Command output:\n${it.output.joinToString(separator = "\n")}"
      }
    }.output.single()
  }
}
