package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"os/exec"
	"strconv"

	"github.com/gin-gonic/gin"
	jsoniter "github.com/json-iterator/go"
)

var json = jsoniter.ConfigCompatibleWithStandardLibrary
var jsonPath = "../django/homepage/progress.json"
var jsonPath_cn = "../golang_nodejs/progress_cn.json"

func writeProcess(username string, identifier string, pageNum int) {
	_, progressMap := readProgress(username, identifier)
	key := username + "=" + identifier
	progressMap[key] = pageNum
	progressJSONStr, err := json.Marshal(progressMap)
	if err != nil {
		fmt.Println(err)
	}
	fmt.Printf("%s\n", progressJSONStr)
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
	str, _ := os.Getwd()
	fmt.Println(str)
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
	// serve static progress_cn.json
	r.StaticFile("/progress_cn.json", jsonPath_cn)

	// will call nodejs to generate progress_cn.json
	r.GET("/all", func(c *gin.Context) {
		output, err := exec.Command("node", "./../nodejs/allProgress.js").Output()
		if err != nil {
			fmt.Println(err)
		}
		fmt.Println(string(output))

		c.Request.URL.Path = "/progress_cn.json"
		r.HandleContext(c)
		/*
			response, err := http.Get("http://127.0.0.1:10000/progress_cn.json")
			if err != nil || response.StatusCode != http.StatusOK {
				c.Status(http.StatusServiceUnavailable)
				return
			}

			reader := response.Body
			contentLength := response.ContentLength
			contentType := response.Header.Get("Content-Type")

			extraHeaders := map[string]string{
				"Content-Disposition": `attachment; filename="all.json"`,
			}

			c.DataFromReader(http.StatusOK, contentLength, contentType, reader, extraHeaders)
		*/
	})

	r.Run(":10000") // listen and serve on 0.0.0.0:8080
}
