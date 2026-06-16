package com.videoeditor.app.core.utils

import android.content.Context
import com.videoeditor.app.core.constants.AppConstants
import com.videoeditor.app.domain.model.Project
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileUtils: FileUtils
) {
    private val projectFile: File
        get() = File(context.filesDir, "projects.json")

    suspend fun saveProject(project: Project) = withContext(Dispatchers.IO) {
        val projects = loadAllProjects().toMutableMap()
        projects[project.id] = project
        saveProjects(projects)
    }

    suspend fun loadAllProjects(): Map<String, Project> = withContext(Dispatchers.IO) {
        if (!projectFile.exists()) return@withContext emptyMap()
        
        try {
            val json = projectFile.readText()
            val array = JSONArray(json)
            val projects = mutableMapOf<String, Project>()
            
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val project = Project(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    thumbnailPath = obj.optString("thumbnailPath", null),
                    createdAt = obj.getLong("createdAt"),
                    updatedAt = obj.getLong("updatedAt"),
                    duration = obj.getLong("duration"),
                    videoClipCount = obj.getInt("videoClipCount")
                )
                projects[project.id] = project
            }
            
            projects
        } catch (e: Exception) {
            emptyMap()
        }
    }

    suspend fun deleteProject(projectId: String) = withContext(Dispatchers.IO) {
        val projects = loadAllProjects().toMutableMap()
        projects.remove(projectId)
        saveProjects(projects)
        fileUtils.deleteProject(context, projectId)
    }

    private fun saveProjects(projects: Map<String, Project>) {
        val array = JSONArray()
        
        projects.values.forEach { project ->
            val obj = JSONObject().apply {
                put("id", project.id)
                put("name", project.name)
                put("thumbnailPath", project.thumbnailPath ?: "")
                put("createdAt", project.createdAt)
                put("updatedAt", project.updatedAt)
                put("duration", project.duration)
                put("videoClipCount", project.videoClipCount)
            }
            array.put(obj)
        }
        
        projectFile.writeText(array.toString())
    }

    fun createNewProject(name: String = AppConstants.DEFAULT_PROJECT_NAME): Project {
        return Project(
            name = name,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    }
}