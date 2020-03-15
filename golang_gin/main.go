package main

import (
	"fmt"
	"io/ioutil"
	"os"
	"strconv"
	"strings"

	"github.com/gin-gonic/gin"
	jsoniter "github.com/json-iterator/go"
)

var json = jsoniter.ConfigCompatibleWithStandardLibrary
var jsonPath = "./progress_cn.json"

func codeTochar(identifier string) string {
	var char_array string
	id_array := strings.Split(identifier, "\\u")
	for _, CharCode := range id_array {
		if len(CharCode) < 1 {
			continue
		}
		true_code, err := strconv.ParseInt(CharCode, 16, 32)
		if err != nil {
			fmt.Println(err)
		}
		char_array += fmt.Sprintf("%c", true_code)
	}
	identifier_cn := char_array
	return identifier_cn
}

func writeProcess(username string, identifier_cn string, pageNum int) {
	_, progressMap := readProgress(username, identifier_cn)
	fmt.Println(progressMap)
	if progressMap == nil {
		progressMap = make(map[string]map[string]int)
	}
	if progressMap[username] == nil {
		progressMap[username] = make(map[string]int)
	}
	progressMap[username][identifier_cn] = pageNum
	progressJSONStr, err := json.MarshalIndent(progressMap, "", "  ")
	if err != nil {
		fmt.Println(err)
	}
	ioutil.WriteFile(jsonPath, progressJSONStr, 0666)

}
func readProgress(username string, identifier_cn string) (int, map[string]map[string]int) {

	progressJSONFile, err := os.OpenFile(jsonPath, os.O_CREATE, 0666)
	if err != nil {
		panic(err)
	}
	defer progressJSONFile.Close()
	progressJSONContent, err := ioutil.ReadAll(progressJSONFile)
	if err != nil {
		fmt.Println(err)
	}
	progressJSON := string(progressJSONContent)

	progressMap := make(map[string]map[string]int)
	json.Unmarshal([]byte(progressJSON), &progressMap)
	fmt.Println(progressMap)
	pageNum := progressMap[username][identifier_cn]
	return pageNum, progressMap
}

func main() {
	r := gin.Default()
	r.GET("/update_progress", func(c *gin.Context) {
		username := c.Query("username")
		identifier := c.Query("identifier")
		identifier_cn := codeTochar(identifier)
		pageNum, _ := strconv.Atoi(c.DefaultQuery("page_num", "0"))
		writeProcess(username, identifier_cn, pageNum)
		c.JSON(200, gin.H{
			"data": "ok",
			"err":  "",
		})
	})
	r.GET("/get_latest_progress", func(c *gin.Context) {
		username := c.Query("username")
		identifier := c.Query("identifier")
		identifier_cn := codeTochar(identifier)
		pageNum, _ := readProgress(username, identifier_cn)
		c.JSON(200, gin.H{
			"page_num": pageNum,
			"err":      "",
		})
	})

	r.Run(":10000") // listen and serve on 0.0.0.0:8080
}
