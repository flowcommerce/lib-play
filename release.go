package main

import (
	"github.com/flowcommerce/tools/executor"
)

func main() {
	executor := executor.Create("lib-play")
	executor = executor.Add("dev tag")
	executor = executor.Add("sbt +publish")
	executor.Run()
}
