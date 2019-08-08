FROM alpine:latest as tag0-test
RUN touch /etc/test-tag0-test
COPY tag0-test .

FROM tag0-test as tag0-missed
RUN touch /etc/test-tag0-missed