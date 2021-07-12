package com.example.sy.cotributorviewer

data class ContributorInfo(
    val login: String,
    val id: String,
    val node_id: String,
    val avatar_url: String,
    val gravatar_id: String,
    val url: String,
    val html_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val starred_url: String,
    val subscriptions_url: String,
    val organizations_url: String,
    val repos_url: String,
    val events_url: String,
    val received_events_url: String,
    val type: String,
    val site_admin: Boolean,
    val contributions: Int) {

    override fun toString(): String {
        val sb = StringBuffer()
        sb.append(login).append(":")
        sb.append(id)
        return sb.toString()
    }
}