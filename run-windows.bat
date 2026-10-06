@echo off
set OLLAMA_MODEL=llama3.2
echo Starting KARPAVAI.AI with Ollama model: %OLLAMA_MODEL%
mvn spring-boot:run
pause
