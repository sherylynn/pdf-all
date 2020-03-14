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
	fmt.Println(identifier_cn)
	return identifier_cn
}

func writeProcess(username string, identifier string, pageNum int) {
	identifier_cn := codeTochar(identifier)
	_, progressMap := readProgress(username, identifier_cn)
	progressMap[username][identifier_cn] = pageNum
	progressJSONStr, err := json.MarshalIndent(progressMap, "", "\t")
	if err != nil {
		fmt.Println(err)
	}
	fmt.Printf("%s\n", progressJSONStr)
	ioutil.WriteFile(jsonPath, progressJSONStr, os.ModeAppend)

}
func readProgress(username string, identifier string) (int, map[string]map[string]int) {
	progressJSONFile, err := ioutil.ReadFile(jsonPath)
	if err != nil {
		fmt.Println(err)
	}
	progressJSON := string(progressJSONFile)
	identifier_cn := codeTochar(identifier)
	progressMap := map[string]map[string]int{}
	json.Unmarshal([]byte(progressJSON), &progressMap)
	pageNum := progressMap[username][identifier_cn]
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

	r.Run(":10000") // listen and serve on 0.0.0.0:8080
}
