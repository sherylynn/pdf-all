package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"strconv"

	"github.com/gin-gonic/gin"
	jsoniter "github.com/json-iterator/go"
)

var json = jsoniter.ConfigCompatibleWithStandardLibrary
var jsonPath = "../django/homepage/progress.json"

func writeProcess(username string, identifier string, pageNum int) {
	_, progressMap := readProgress(username, identifier)
	key := username + "=" + identifier
	progressMap[key] = pageNum
	progressJSONStr, err := json.Marshal(progressMap)
	if err != nil {
		fmt.Println(err)
	}
	ioutil.WriteFile(jsonPath, progressJSONStr, os.ModeAppend)

}

func readProgress(username string, identifier string) (int, map[string]int) {
	progressJSONFile, err := ioutil.ReadFile(jsonPath)
	if err != nil {
		fmt.Println(err)
	}
	progressJSON := string(progressJSONFile)
	key := username + "=" + identifier
	progressMap := make(map[string]int)
	json.Unmarshal([]byte(progressJSON), &progressMap)
	pageNum := progressMap[key]
	return pageNum, progressMap
}

func main() {
	r := gin.Default()
	r.GET("/update_progress", func(c *gin.Context) {
		username := c.Query("username")
		identifier := c.Query("identifier")
		pageNum, _ := strconv.Atoi(c.DefaultQuery("page_num", "0"))
		writeProcess(username, identifier, pageNum)
		fmt.Println(username)
		c.JSON(200, gin.H{
			"data": "ok",
			"err":  "",
		})
	})
	r.GET("/get_latest_progress", func(c *gin.Context) {
		username := c.Query("username")
		identifier := c.Query("identifier")
		pageNum, _ := readProgress(username, identifier)
		c.JSON(200, gin.H{
			"page_num": pageNum,
			"err":      "",
		})
	})
	r.Run(":10000") // listen and serve on 0.0.0.0:8080
}
