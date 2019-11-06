package cosmic.com.mapprj.model

import com.google.gson.annotations.SerializedName

class BlogInfo(@field:SerializedName("title")
               var title: String, @field:SerializedName("link")
               var link: String, @field:SerializedName("description")
               var description: String)
