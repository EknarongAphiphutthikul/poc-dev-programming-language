FROM golang:1.19.6-alpine3.16

WORKDIR /usr/src/app

RUN mkdir -p "/data/app"

COPY go.mod go.sum ./
RUN go mod download && go mod verify

COPY . .
RUN CGO_ENABLED=0 GOOS=linux go build -ldflags="-s -w" -v -o /usr/local/bin/app .

CMD ["app"]