selectUbyLid
===
select id from bbsuser where lid=#lid#

selectUpwbyLid
===
select id,lid,pw from bbsuser where lid=#lid#

listPost
===
SELECT a.id,a.tm,a.cont,a.ngd,a.lid
FROM bbspost a where a.sta='1' 
@if(isNotEmpty(uid)&&isNotEmpty(pty)&&"3"==pty){
and a.uid=#uid#
@}
@else {
    @if(isNotEmpty(pty)){
    and (a.pty=#pty# and a.uid is null)
    @}
    @else {
       and (a.pty='1' and a.uid is null)
    @}
@}
@if(isNotEmpty(searchkey)){
and (a.tm like '%#text(searchkey)#%' or a.lid like '%#text(searchkey)#%')
@}
order by ngd desc limit #text(start)#,#text(limit)# 

listPostCount
===
SELECT count(*)
FROM bbspost a where a.sta='1'
@if(isNotEmpty(lid)&&isNotEmpty(pty)&&"3"==pty){
and a.lid=#lid#
@}
@else {
    @if(isNotEmpty(pty)){
    and (a.pty=#pty# and a.uid is null)
    @}
    @else {
       and (a.pty='1' and a.uid is null)
    @}
@}
@if(isNotEmpty(searchkey)){
and (a.tm like '%#text(searchkey)#%' or a.lid like '%#text(searchkey)#%')
@}

listPostFile
===
select  id, pkid, fpa, fname, fngd, fs, sn from bbsattach where pkid=#pkid# order by sn